package leavesc.reactivehttp.weather.core.http

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import leavesc.reactivehttp.core.callback.RequestCallback
import leavesc.reactivehttp.core.callback.RequestPairCallback
import leavesc.reactivehttp.core.datasource.RemoteDataSource
import leavesc.reactivehttp.core.datasource.RemoteExtendDataSource
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent
import leavesc.reactivehttp.weather.core.http.base.HttpResBean
import leavesc.reactivehttp.weather.core.model.DistrictBean
import leavesc.reactivehttp.weather.core.model.ForecastsBean
import kotlin.random.Random

/**
 * 作者：leavesC
 * 时间：2019/5/31 14:27
 * 描述：
 */
class MapDataSource(actionEventEvent: IUIActionEvent) : RemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    fun getProvince(callback: RequestCallback<List<DistrictBean>>) {
        execute(callback) {
            getService().getProvince()
        }
    }

    fun getCity(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        execute(callback) {
            getService().getCity(keywords)
        }
    }

    fun getCounty(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        execute(callback) {
            getService().getCounty(keywords)
        }
    }

}

class WeatherDataSource(actionEventEvent: IUIActionEvent) : RemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    fun getWeather(city: String, callback: RequestCallback<List<ForecastsBean>>) {
        execute(callback) {
            getService().getWeather(city)
        }
    }

}

class TestDataSource(actionEventEvent: IUIActionEvent) : RemoteExtendDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    private suspend fun testDelay(): HttpResBean<String> {
        withIO {
            delay(3000)
        }
        return HttpResBean(1, "msg", "data coming")
    }

    fun testDelay(callback: RequestCallback<String>): Job {
        return execute(callback) {
            testDelay()
        }
    }

    fun testPair(callback: RequestPairCallback<List<ForecastsBean>, String>) {
        execute(callback, showLoading = false, block1 = { getService().getWeather("411122") }, block2 = {
            delay(1000)
            HttpResBean(1, "errMsg", "data coming")
        })
    }

}