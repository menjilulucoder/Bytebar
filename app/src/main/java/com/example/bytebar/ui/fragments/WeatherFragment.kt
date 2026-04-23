package com.example.bytebar.ui.fragments

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bytebar.ai.WeatherApi
import com.example.bytebar.ai.WeatherCodeMapper
import com.example.bytebar.databinding.FragmentWeatherBinding
import com.example.bytebar.model.weather.ForecastDay
import com.example.bytebar.model.weather.WeatherResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherApi: WeatherApi
    private val defaultLat = 23.1291
    private val defaultLon = 113.2644

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        weatherApi = WeatherApi()
        loadWeather()

        return binding.root
    }

    private fun loadWeather() {
        binding.loadingProgress.visibility = View.VISIBLE

        weatherApi.fetchWeather(defaultLat, defaultLon, object : WeatherApi.Callback {
            override fun onSuccess(result: WeatherResult) {
                binding.loadingProgress.visibility = View.GONE
                val now = result.now

                binding.cityText.text = now.city
                binding.updateTimeText.text = buildUpdateTimeText()
                binding.temperatureText.text = now.temperature
                binding.weatherText.text = now.weatherText
                binding.windText.text = now.windText
                binding.humidityText.text = now.humidityText
                binding.pressureText.text = now.pressureText
                binding.airText.text = now.airText
                binding.currentWeatherIcon.setImageResource(
                    WeatherCodeMapper.toIconRes(now.weatherCode, now.isDay)
                )
                binding.weatherImage.setImageResource(
                    WeatherCodeMapper.toBackgroundRes(now.weatherCode, now.isDay)
                )

                renderForecast(result.forecast, now.isDay)
            }

            override fun onError(error: String) {
                binding.loadingProgress.visibility = View.GONE
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buildUpdateTimeText(): String {
        val formatter = SimpleDateFormat("HH:mm 更新", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun renderForecast(forecast: List<ForecastDay>, isDay: Boolean) {
        binding.llForecast.removeAllViews()
        for (day in forecast) {
            binding.llForecast.addView(createForecastItem(day, isDay))
        }
    }

    private fun createForecastItem(day: ForecastDay, isDay: Boolean): View {
        val context = requireContext()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 30, 36, 30)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 24
            layoutParams = params
            setBackgroundResource(com.example.bytebar.R.drawable.bg_forecast_card)
            gravity = android.view.Gravity.CENTER
        }

        val dateText = TextView(context).apply {
            text = day.date
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textSize = 17f
            gravity = android.view.Gravity.CENTER
        }

        val icon = ImageView(context).apply {
            val iconParams = LinearLayout.LayoutParams(96, 96)
            iconParams.topMargin = 14
            iconParams.bottomMargin = 14
            layoutParams = iconParams
            setImageResource(WeatherCodeMapper.toIconRes(day.weatherCode, isDay))
            setColorFilter(ContextCompat.getColor(context, android.R.color.white))
            contentDescription = day.rainChanceText
        }

        val descText = TextView(context).apply {
            text = WeatherCodeMapper.toWeatherText(day.weatherCode)
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textSize = 16f
            gravity = android.view.Gravity.CENTER
        }

        val tempText = TextView(context).apply {
            text = "${day.maxTemp}/${day.minTemp}"
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textSize = 17f
            gravity = android.view.Gravity.CENTER
        }

        val rainText = TextView(context).apply {
            text = day.rainChanceText
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textSize = 15f
            gravity = android.view.Gravity.CENTER
        }

        container.addView(dateText)
        container.addView(icon)
        container.addView(descText)
        container.addView(tempText)
        container.addView(rainText)
        return container
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
