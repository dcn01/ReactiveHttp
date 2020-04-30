package leavesc.reactivehttp.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:18
 * 描述：
 */
internal object RetrofitManagement {

    lateinit var serverUrl: String

    lateinit var okHttpClient: OkHttpClient

    private val serviceMap = ConcurrentHashMap<String, Any>()

    private fun createRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun <T : Any> getService(clz: Class<T>, host: String): T {
        val realHost = if (host.isBlank()) serverUrl else host
        //以 host 路径 + ApiService 的类路径作为 key
        val key = realHost + clz.canonicalName
        if (serviceMap.containsKey(key)) {
            return serviceMap[key] as T
        }
        val value = createRetrofit(realHost).create(clz)
        serviceMap[key] = value
        return value
    }

}