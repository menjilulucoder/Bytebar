package com.example.bytebar.model.weather

/**
 * 天气请求返回结果
 */
data class WeatherResult(
    val now: WeatherNow,
    val forecast: List<ForecastDay>
)
