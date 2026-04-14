package com.example.bytebar.ai

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resumeWithException

class SparkApi {
    companion object {
        private const val APP_ID = "6509fe84"
        private const val API_KEY = "9e522c3b53f15ec60a3e551c3090a03e"
        private const val API_SECRET = "NWE2ZDcxYmNjYzViZDkxZThjYzMyMGQ2"
        private const val HOST = "spark-api.xf-yun.com"
        private const val PATH = "/v3.5/chat"
        private const val WS_URL = "wss://$HOST$PATH"

        /**
         * 协程版本的聊天方法
         */
        suspend fun getChatResponse(prompt: String): String {
            return with(SparkApi()) {
                suspendCancellableCoroutine { continuation ->
                    chat(prompt, object : Callback {
                        override fun onSuccess(response: String) {
                            continuation.resume(response, null)
                        }
                        
                        override fun onError(error: String) {
                            continuation.resumeWithException(Exception(error))
                        }
                    })
                }
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .readTimeout(180, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val mainHandler = Handler(Looper.getMainLooper())

    interface Callback {
        fun onSuccess(response: String)
        fun onError(error: String)
    }

    fun chat(prompt: String, callback: Callback) {
        val wsUrl = getAuthUrl(WS_URL, HOST)

        val webSocket = client.newWebSocket(
            Request.Builder().url(wsUrl).build(),
            object : WebSocketListener() {
                private val responseBuilder = StringBuilder()
                private var lastStatus = 1
                private var callbackCalled = false

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    val requestBody = JSONObject().apply {
                        put("header", JSONObject().apply {
                            put("app_id", APP_ID)
                        })
                        put("parameter", JSONObject().apply {
                            put("chat", JSONObject().apply {
                                put("domain", "generalv3.5")
                                put("temperature", 0.5)
                                put("max_tokens", 2048)
                            })
                        })
                        put("payload", JSONObject().apply {
                            put("message", JSONObject().apply {
                                put("text", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("role", "user")
                                        put("content", prompt)
                                    })
                                })
                            })
                        })
                    }

                    webSocket.send(requestBody.toString())
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    android.util.Log.d("SparkApi", "收到消息: $text")
                    
                    val json = JSONObject(text)
                    val header = json.optJSONObject("header")
                    val code = header?.optInt("code")

                    if (code != null && code != 0) {
                        if (!callbackCalled) {
                            callbackCalled = true
                            val message = header.optString("message", "未知错误")
                            mainHandler.post { callback.onError("API错误: $code, $message") }
                        }
                        webSocket.close(1000, "")
                        return
                    }

                    val payload = json.optJSONObject("payload")
                    val choices = payload?.optJSONObject("choices")
                    val textArray = choices?.optJSONArray("text")
                    val content = textArray?.optJSONObject(0)?.optString("content")

                    if (content != null) {
                        responseBuilder.append(content)
                        android.util.Log.d("SparkApi", "添加内容: $content")
                    }

                    // 检查状态字段
                    val status = choices?.optInt("status") ?: 1
                    lastStatus = status
                    android.util.Log.d("SparkApi", "Status: $lastStatus, Length: ${responseBuilder.length}")

                    // 当状态为2时，表示完成
                    if (lastStatus == 2) {
                        val finalContent = responseBuilder.toString()
                        if (finalContent.isNotEmpty() && !callbackCalled) {
                            callbackCalled = true
                            android.util.Log.d("SparkApi", "完成！内容: ${finalContent.take(100)}")
                            mainHandler.post { callback.onSuccess(finalContent) }
                        }
                        webSocket.close(1000, "完成")
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    android.util.Log.d("SparkApi", "正在关闭: $code $reason")
                    val content = responseBuilder.toString()
                    if (content.isNotEmpty() && !callbackCalled) {
                        callbackCalled = true
                        mainHandler.post { callback.onSuccess(content) }
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    val content = responseBuilder.toString()
                    if (!callbackCalled) {
                        callbackCalled = true
                        if (content.isNotEmpty()) {
                            mainHandler.post { callback.onSuccess(content) }
                        } else {
                            mainHandler.post { callback.onError("连接失败: ${t.message}") }
                        }
                    }
                }
            }
        )
    }

    private fun getAuthUrl(url: String, host: String): String {
        val date = java.util.Date()
        val dateFormat = java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US)
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("GMT")
        val dateStr = dateFormat.format(date)

        val signatureOrigin = "host: $host\ndate: $dateStr\nGET $PATH HTTP/1.1"
        val signatureSha = hmacSha256(signatureOrigin, API_SECRET)
        val signatureShaBase64 = Base64.getEncoder().encodeToString(signatureSha)

        val authorizationOrigin = "api_key=\"$API_KEY\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"$signatureShaBase64\""
        val authorization = Base64.getEncoder().encodeToString(authorizationOrigin.toByteArray(StandardCharsets.UTF_8))

        return "$url?authorization=${URLEncoder.encode(authorization, "UTF-8")}&date=${URLEncoder.encode(dateStr, "UTF-8")}&host=$host"
    }

    private fun hmacSha256(data: String, key: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(secretKey)
        return mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    }
}