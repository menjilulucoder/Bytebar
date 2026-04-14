package com.example.bytebar.ui.weather

/**
 * 天气模块的MVP契约接口
 */
interface WeatherContract {

    /**
     * View接口，定义UI操作
     */
    interface View {
        /**
         * 显示天气数据
         */
        fun showWeatherData(city: String, temperature: String, weather: String)

        /**
         * 显示加载状态
         */
        fun showLoading()

        /**
         * 隐藏加载状态
         */
        fun hideLoading()

        /**
         * 显示错误信息
         */
        fun showError(message: String)
    }

    /**
     * Presenter接口，定义业务逻辑
     */
    interface Presenter {
        /**
         * 加载天气数据
         */
        fun loadWeather()

        /**
         * 销毁Presenter
         */
        fun destroy()
    }
}