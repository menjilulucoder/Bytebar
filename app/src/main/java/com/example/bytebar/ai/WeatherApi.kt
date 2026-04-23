package com.example.bytebar.ai

import android.os.Handler
import android.os.Looper
import com.example.bytebar.model.weather.ForecastDay
import com.example.bytebar.model.weather.WeatherNow
import com.example.bytebar.model.weather.WeatherResult
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class WeatherApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val mainHandler = Handler(Looper.getMainLooper())
    
    interface Callback {
        fun onSuccess(result: WeatherResult)
        fun onError(error: String)
    }
    
    fun fetchWeather(latitude: Double, longitude: Double, callback: Callback) {
        Thread {
            try {
                val url = buildUrl(latitude, longitude)
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
                    if (body.isNullOrBlank()) {
                        mainHandler.post { callback.onError("响应为空") }
                        return@use
                    }

                    val result = parseWeather(JSONObject(body), latitude, longitude)
                    mainHandler.post { callback.onSuccess(result) }
                }
            } catch (e: Exception) {
                mainHandler.post { callback.onError("网络错误: ${e.message}") }
            }
        }.start()
    }

    private fun buildUrl(latitude: Double, longitude: Double): String {
        return "https://api.open-meteo.com/v1/forecast" +
            "?latitude=$latitude" +
            "&longitude=$longitude" +
            "&current=temperature_2m,relative_humidity_2m,pressure_msl,wind_speed_10m,wind_direction_10m,weather_code,is_day" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
            "&forecast_days=5" +
            "&timezone=Asia/Shanghai"
    }

    private fun parseWeather(json: JSONObject, latitude: Double, longitude: Double): WeatherResult {
        val current = json.optJSONObject("current") ?: throw IllegalStateException("当前天气数据为空")
        val daily = json.optJSONObject("daily") ?: throw IllegalStateException("预报数据为空")

        val weatherCode = current.optInt("weather_code", -1)
        val isDay = current.optInt("is_day", 1) == 1
        val temperature = "${current.optDouble("temperature_2m", 0.0).toInt()}°C"
        val humidity = "湿度: ${current.optInt("relative_humidity_2m", 0)}%"
        val pressure = "气压: ${current.optInt("pressure_msl", 0)}hPa"
        val windSpeed = current.optDouble("wind_speed_10m", 0.0)
        val windDirection = current.optDouble("wind_direction_10m", 0.0)
        val weatherText = WeatherCodeMapper.toWeatherText(weatherCode)

        val now = WeatherNow(
            city = mapCityName(latitude, longitude),
            temperature = temperature,
            weatherText = weatherText,
            windText = String.format(Locale.getDefault(), "%.1fm/s %s", windSpeed, getWindDirection(windDirection)),
            humidityText = humidity,
            pressureText = pressure,
            airText = "空气质量: 62（良）",
            weatherCode = weatherCode,
            isDay = isDay
        )

        val timeArray = daily.optJSONArray("time") ?: JSONArray()
        val codeArray = daily.optJSONArray("weather_code") ?: JSONArray()
        val maxArray = daily.optJSONArray("temperature_2m_max") ?: JSONArray()
        val minArray = daily.optJSONArray("temperature_2m_min") ?: JSONArray()
        val rainArray = daily.optJSONArray("precipitation_probability_max") ?: JSONArray()

        val count = listOf(timeArray.length(), codeArray.length(), maxArray.length(), minArray.length(), rainArray.length())
            .minOrNull() ?: 0
        val forecast = mutableListOf<ForecastDay>()

        for (index in 0 until count) {
            val code = codeArray.optInt(index, -1)
            forecast.add(
                ForecastDay(
                    date = formatDate(timeArray.optString(index, "")),
                    weatherCode = code,
                    maxTemp = "${maxArray.optDouble(index, 0.0).toInt()}°",
                    minTemp = "${minArray.optDouble(index, 0.0).toInt()}°",
                    rainChanceText = "降水 ${rainArray.optInt(index, 0)}%"
                )
            )
        }

        return WeatherResult(now = now, forecast = forecast)
    }
    
    private fun getWindDirection(degree: Double): String {
        val directions = arrayOf("北", "东北", "东", "东南", "南", "西南", "西", "西北")
        val index = ((degree + 22.5) % 360 / 45).toInt()
        return directions[index.coerceIn(0, directions.lastIndex)]
    }

    private fun formatDate(origin: String): String {
        if (origin.isBlank()) return "--"
        return try {
            val source = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val target = SimpleDateFormat("MM/dd", Locale.getDefault())
            val date = source.parse(origin) ?: return origin
            target.format(date)
        } catch (_: Exception) {
            origin
        }
    }

    private fun mapCityName(latitude: Double, longitude: Double): String {
        return when {
            kotlin.math.abs(latitude - 23.1291) < 0.5 && kotlin.math.abs(longitude - 113.2644) < 0.5 -> "广州"
            else -> "当前位置"
        }
    }
}
