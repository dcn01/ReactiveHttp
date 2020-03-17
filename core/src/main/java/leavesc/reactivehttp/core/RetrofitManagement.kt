package leavesc.reactivehttp.core

import leavesc.hello.monitor.MonitorInterceptor
import leavesc.reactivehttp.core.holder.ContextHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:18
 * 描述：
 */
object RetrofitManagement {

    private const val READ_TIMEOUT = 10000L

    private const val WRITE_TIMEOUT = 10000L

    private const val CONNECT_TIMEOUT = 10000L

    private val serviceMap = ConcurrentHashMap<String, Any>()

    private fun createRetrofit(url: String): Retrofit {
        val builder = OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(httpLoggingInterceptor)
        //这是我的另外一个开源库：https://github.com/leavesC/Monitor
        builder.addInterceptor(MonitorInterceptor(ContextHolder.context))
        builder.addInterceptor(FilterInterceptor())
        val client = builder.build()
        return Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    internal fun <T : Any> getService(clz: Class<T>, host: String): T {
        //以 host 路径 + ApiService 的类路径作为 key
        val key = host + clz.canonicalName
        if (serviceMap.containsKey(key)) {
            return serviceMap[key] as T
        }
        val value = createRetrofit(host).create(clz)
        serviceMap[key] = value
        return value
    }

}