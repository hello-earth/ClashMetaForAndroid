package com.github.kr328.clash

import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.SettingsDesign
import com.github.kr328.clash.design.dialog.requestModelTextInput
import com.github.kr328.clash.design.util.ValidatorHttpUrl
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

class SettingsActivity : BaseActivity<SettingsDesign>() {
    override suspend fun main() {
        val design = SettingsDesign(this)

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        SettingsDesign.Request.StartApp ->
                            startActivity(AppSettingsActivity::class.intent)
                        SettingsDesign.Request.StartNetwork ->
                            startActivity(NetworkSettingsActivity::class.intent)
                        SettingsDesign.Request.StartOverride ->
                            startActivity(OverrideSettingsActivity::class.intent)
                        SettingsDesign.Request.StartMetaFeature ->
                            startActivity(MetaFeatureSettingsActivity::class.intent)
                        SettingsDesign.Request.InputWebHookUrl ->{
                            launch {
                                val url = requestModelTextInput(
                                    uiStore.hookUrl,
                                    getString(R.string.hook_url),
                                    getString(R.string.accept_http_content),
                                    validator = ValidatorHttpUrl
                                )
                                if (url.isNotEmpty() && url!="https://") {
                                    uiStore.hookUrl = url
                                    Log.e(uiStore.hookUrl)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}