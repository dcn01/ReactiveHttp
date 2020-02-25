package leavesc.reactivehttp.weather.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_weather.*
import leavesc.reactivehttp.core.viewmodel.BaseViewModel
import leavesc.reactivehttp.weather.R
import leavesc.reactivehttp.weather.adapter.WeatherAdapter
import leavesc.reactivehttp.weather.core.cache.AreaCache
import leavesc.reactivehttp.weather.core.model.CastsBean
import leavesc.reactivehttp.weather.core.model.ForecastsBean
import leavesc.reactivehttp.weather.core.view.BaseActivity
import leavesc.reactivehttp.weather.core.viewmodel.WeatherViewModel

/**
 * 作者：leavesC
 * 时间：2019/6/2 20:18
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class WeatherActivity : BaseActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    private val castsBeanList = mutableListOf<CastsBean>()

    private val weatherAdapter = WeatherAdapter(castsBeanList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        rv_dailyForecast.layoutManager = LinearLayoutManager(this)
        rv_dailyForecast.adapter = weatherAdapter
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.getWeather(AreaCache.getAdCode(this))
        }
        iv_place.setOnClickListener {
            startActivity(MapActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.getWeather(AreaCache.getAdCode(this))
    }

    override fun initViewModel(): BaseViewModel? {
        weatherViewModel = getViewModel(WeatherViewModel::class.java)
        weatherViewModel.forecastsBeanLiveData.observe(this, Observer {
            showWeather(it)
        })
        return weatherViewModel
    }

    private fun showWeather(forecastsBean: ForecastsBean) {
        tv_city.text = forecastsBean.city
        castsBeanList.clear()
        castsBeanList.addAll(forecastsBean.casts)
        weatherAdapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

}