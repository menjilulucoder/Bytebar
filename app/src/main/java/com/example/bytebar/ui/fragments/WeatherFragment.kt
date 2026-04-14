package com.example.bytebar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bytebar.ai.WeatherApi
import com.example.bytebar.databinding.FragmentWeatherBinding

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
            override fun onSuccess(city: String, temperature: String, weather: String, wind: String, humidity: String, pressure: String, airQuality: String) {
                binding.loadingProgress.visibility = View.GONE
                binding.cityText.text = city
                binding.temperatureText.text = temperature
                binding.weatherText.text = weather
                binding.windText.text = wind
                binding.humidityText.text = humidity
                binding.pressureText.text = pressure
                binding.airText.text = airQuality
            }

            override fun onError(error: String) {
                binding.loadingProgress.visibility = View.GONE
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}