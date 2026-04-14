package com.example.bytebar.ui.weather

/**
 * 天气模块的Presenter，实现业务逻辑
 */
class WeatherPresenter(private val view: WeatherContract.View) : WeatherContract.Presenter {

    override fun loadWeather() {
        // 显示加载状态
        view.showLoading()

        // 模拟网络请求获取天气数据
        // 实际项目中这里会调用天气API
        // 由于是模拟，直接返回假数据
        view.showWeatherData("北京市", "25°C", "晴")

        // 隐藏加载状态
        view.hideLoading()
    }

    override fun destroy() {
        // 清理资源
    }
}