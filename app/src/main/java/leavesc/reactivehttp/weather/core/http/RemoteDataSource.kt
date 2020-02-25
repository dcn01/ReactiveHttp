package leavesc.reactivehttp.weather.core.http

import leavesc.reactivehttp.core.BaseRemoteDataSource
import leavesc.reactivehttp.core.viewmodel.IBaseViewModeEventScope
import leavesc.reactivehttp.core.RequestCallback
import leavesc.reactivehttp.weather.core.model.DistrictBean
import leavesc.reactivehttp.weather.core.model.ForecastsBean

/**
 * 作者：leavesC
 * 时间：2019/5/31 14:27
 * 描述：
 */
class MapDataSource(baseViewModelEventEvent: IBaseViewModeEventScope) : BaseRemoteDataSource<ApiService>(baseViewModelEventEvent, ApiService::class.java) {

    fun getProvince(callback: RequestCallback<List<DistrictBean>>) {
        execute({ getService().getProvince() }, callback)
    }

    fun getCity(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        execute({ getService().getCity(keywords) }, callback)
    }

    fun getCounty(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        execute({ getService().getCounty(keywords) }, callback)
    }

}

class WeatherDataSource(baseViewModelEventEvent: IBaseViewModeEventScope) : BaseRemoteDataSource<ApiService>(baseViewModelEventEvent, ApiService::class.java) {

    fun getWeather(city: String, callback: RequestCallback<List<ForecastsBean>>) {
        execute({ getService().getWeather(city) }, callback)
    }

}