package com.example.bytebar.ai

import com.example.bytebar.R

/**
 * Open-Meteo weather_code 到文案/图标的映射
 */
object WeatherCodeMapper {

    fun toWeatherText(code: Int): String {
        return when (code) {
            0 -> "晴"
            1 -> "大部晴朗"
            2 -> "局部多云"
            3 -> "阴"
            45, 48 -> "雾"
            51, 53, 55, 56, 57 -> "毛毛雨"
            61, 63, 65, 66, 67, 80, 81, 82 -> "雨"
            71, 73, 75, 77, 85, 86 -> "雪"
            95, 96, 99 -> "雷暴"
            else -> "未知"
        }
    }

    fun toIconRes(code: Int, isDay: Boolean): Int {
        return when (code) {
            0 -> if (isDay) R.drawable.ic_weather_sunny else R.drawable.ic_weather_night
            1, 2 -> R.drawable.ic_weather_partly_cloudy
            3 -> R.drawable.ic_weather_cloudy
            45, 48 -> R.drawable.ic_weather_fog
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.ic_weather_rain
            71, 73, 75, 77, 85, 86 -> R.drawable.ic_weather_snow
            95, 96, 99 -> R.drawable.ic_weather_thunder
            else -> R.drawable.ic_weather_cloudy
        }
    }

    fun toBackgroundRes(code: Int, isDay: Boolean): Int {
        return when {
            !isDay -> R.drawable.bg_overcast
            code == 0 -> R.drawable.bg_sunny
            else -> R.drawable.bg_overcast
        }
    }
}
