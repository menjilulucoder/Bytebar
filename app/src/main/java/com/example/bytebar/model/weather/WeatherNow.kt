package com.example.bytebar.model.weather

/**
 * 当前天气数据
 */
data class WeatherNow(
    val city: String,
    val temperature: String,
    val weatherText: String,
    val windText: String,
    val humidityText: String,
    val pressureText: String,
    val airText: String,
    val weatherCode: Int,
    val isDay: Boolean
)
