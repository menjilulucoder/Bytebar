package com.example.bytebar.model.weather

/**
 * 单日天气预报数据
 */
data class ForecastDay(
    val date: String,
    val weatherCode: Int,
    val maxTemp: String,
    val minTemp: String,
    val rainChanceText: String
)
