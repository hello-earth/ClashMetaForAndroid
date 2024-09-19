package com.github.kr328.clash.service.util

import com.github.kr328.clash.common.log.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException


fun selectProxy(port: String, proxyName: String, switchName: String) {
    var url = "http://127.0.0.1:$port/proxies/$proxyName"
    val client = OkHttpClient()
    try {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val json = "{\"name\":\"$switchName\"}"
        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url).put(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理失败情况
                Log.e(e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {

            }
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
