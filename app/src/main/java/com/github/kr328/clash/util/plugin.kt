package com.github.kr328.clash.util

import com.github.kr328.clash.common.log.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException


fun reportMe(url : String?){
    if (url == null)  return
    val client = OkHttpClient()
    try {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理失败情况
                Log.d("onFailure: Failed to execute request")
            }
            override fun onResponse(call: Call, response: Response) {

            }
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}