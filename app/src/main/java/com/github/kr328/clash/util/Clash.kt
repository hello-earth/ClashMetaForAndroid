package com.github.kr328.clash.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.Looper
import com.github.kr328.clash.common.compat.startForegroundServiceCompat
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.service.ClashService
import com.github.kr328.clash.service.TunService
import com.github.kr328.clash.service.util.sendBroadcastSelf

fun Context.startClashService(): Intent? {
    reportMe(UiStore(this).hookUrl)
    val startTun = UiStore(this).enableVpn

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest
        startForegroundService(this, TunService::class.intent)
    } else {
        startForegroundService(this, ClashService::class.intent)
    }

    return null
}

fun startForegroundService(context: Context, intent: Intent) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        context.startForegroundServiceCompat(intent)
    }, 1500)
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP))
}