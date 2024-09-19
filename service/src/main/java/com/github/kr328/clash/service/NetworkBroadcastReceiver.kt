package com.github.kr328.clash.service


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.text.TextUtils
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.yamlMap
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.service.util.processingDir
import com.github.kr328.clash.service.util.selectProxy
import java.io.BufferedReader
import java.io.FileReader


class NetworkBroadcastReceiver : BroadcastReceiver() {
    private var wifiStateChanged = false
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (TextUtils.equals(action, "android.net.wifi.STATE_CHANGE")) {
            wifiStateChanged = true
        }
        if (wifiStateChanged && TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
            wifiStateChanged = false
            var ssidName = getSSID(context)
            if (ssidName.isEmpty()) return
            ssidName = ssidName.replace("\"", "")
            val filename = context.processingDir.absolutePath + "/config.yaml"
            val br = BufferedReader(FileReader(filename))
            val content = br.use { it.readText() }.replace("---\n", "")
            val rawcfg = Yaml.default.parseToYamlNode(content)

            var port = "9090"
            val controller = rawcfg.yamlMap.get<YamlNode>("external-controller")
            if (controller != null) {
                port = controller.contentToString().replace("'","").split(":")[1]
            }
            var groups = rawcfg.yamlMap.get<YamlList>("proxy-groups") as YamlList

            for (item in groups.items) {
                var ssidPolicy = item.yamlMap.get<YamlMap>("ssid-policy")
                if (ssidPolicy != null) {
                    var groupName: YamlNode? = item.yamlMap.get<YamlNode>("name") ?: continue
                    if (groupName == null) continue
                    var switchName = ""
                    for (entry in ssidPolicy.entries) {
                        if (ssidName == entry.key.content) {
                            selectProxy(
                                port,
                                groupName.contentToString().replace("'", ""),
                                entry.value.contentToString().replace("'", "")
                            )
                            return
                        } else if ("default" == entry.key.content) {
                            switchName = entry.value.contentToString().replace("'", "")
                        }
                    }
                    if (switchName.isNotEmpty()) {
                        selectProxy(
                            port,
                            groupName.contentToString().replace("'", ""),
                            switchName
                        )
                    }
                }
            }
        }
    }

    fun getSSID(mContext: Context): String {
        val wifiManager = mContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.getConnectionInfo()
        return connectionInfo.ssid
    }

}
