package leavesc.reactivehttp.weather

import android.app.Application
import leavesc.hello.monitor.MonitorInterceptor
import leavesc.reactivehttp.core.ReactiveHttp
import leavesc.reactivehttp.weather.core.http.base.FilterInterceptor
import leavesc.reactivehttp.weather.core.http.base.HttpConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:07
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ReactiveHttp.Builder(this, HttpConfig.BASE_URL_MAP).okHttClient(createHttpClient()).build().init()
    }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .readTimeout(1000L, TimeUnit.MILLISECONDS)
                .writeTimeout(1000L, TimeUnit.MILLISECONDS)
                .connectTimeout(1000L, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(httpLoggingInterceptor)
        //这是我的另外一个开源库：https://github.com/leavesC/Monitor
        builder.addInterceptor(MonitorInterceptor(this))
        builder.addInterceptor(FilterInterceptor())
        return builder.build()
    }

}