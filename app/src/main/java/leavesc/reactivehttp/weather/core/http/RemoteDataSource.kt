package leavesc.reactivehttp.weather.core.http

import leavesc.reactivehttp.core.BaseRemoteDataSource
import leavesc.reactivehttp.core.callback.RequestCallback
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent
import leavesc.reactivehttp.weather.core.model.DistrictBean
import leavesc.reactivehttp.weather.core.model.ForecastsBean

/**
 * 作者：leavesC
 * 时间：2019/5/31 14:27
 * 描述：
 */
class MapDataSource(actionEventEvent: IUIActionEvent) : BaseRemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

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

class WeatherDataSource(actionEventEvent: IUIActionEvent) : BaseRemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    fun getWeather(city: String, callback: RequestCallback<List<ForecastsBean>>) {
        execute(callback) {
            getService().getWeather(city)
        }
    }

}