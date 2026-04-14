package com.example.bytebar.ai

import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WeatherApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val mainHandler = Handler(Looper.getMainLooper())
    
    interface Callback {
        fun onSuccess(city: String, temperature: String, weather: String, wind: String, humidity: String, pressure: String, airQuality: String)
        fun onError(error: String)
    }
    
    fun fetchWeather(latitude: Double, longitude: Double, callback: Callback) {
        Thread {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,showers,snowfall,cloud_cover,wind_speed_10m,wind_direction_10m,pressure_msl&timezone=Asia/Shanghai"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        mainHandler.post { callback.onError("请求失败: ${response.code}") }
                        return@Thread
                    }
                    
                    val body = response.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        val current = json.optJSONObject("current")
                        if (current != null) {
                            val city = "广州"
                            val temperature = current.optString("temperature_2m", "0") + "°C"
                            val humidity = "湿度: " + current.optString("relative_humidity_2m", "0") + "%"
                            val pressure = "气压: " + current.optString("pressure_msl", "0") + "hPa"
                            val windSpeed = current.optString("wind_speed_10m", "0")
                            val windDirection = current.optString("wind_direction_10m", "0")
                            val wind = "${windSpeed}m/s ${getWindDirection(windDirection.toDouble())}"
                            val weather = "晴"
                            val airQuality = "空气质量: 良好"
                            
                            mainHandler.post {
                                callback.onSuccess(city, temperature, weather, wind, humidity, pressure, airQuality)
                            }
                        } else {
                            mainHandler.post { callback.onError("未找到天气数据") }
                        }
                    } else {
                        mainHandler.post { callback.onError("响应为空") }
                    }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError("网络错误: ${e.message}") }
            }
        }.start()
    }
    
    private fun getWindDirection(degree: Double): String {
        val directions = arrayOf("北", "东北", "东", "东南", "南", "西南", "西", "西北")
        val index = ((degree + 22.5) % 360 / 45).toInt()
        return directions[index]
    }
}