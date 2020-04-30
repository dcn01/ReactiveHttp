package leavesc.reactivehttp.core

import android.content.Context
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.holder.ContextHolder
import leavesc.reactivehttp.core.holder.HttpActionHolder
import okhttp3.OkHttpClient
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * 作者：CZY
 * 时间：2020/4/30 15:29
 * 描述：
 */
class ReactiveHttp internal constructor(builder: Builder) {

    private val context = builder.context.applicationContext

    private val formatExceptionFun = builder.formatExceptionFun ?: ::formatException

    private val serverUrl = builder.serverUrl

    private val okHttpClient = builder.okHttClient ?: createDefaultOkHttpClient()

    fun init() {
        ContextHolder.context = context
        HttpActionHolder.formatExceptionFun = formatExceptionFun
        RetrofitManagement.serverUrl = serverUrl
        RetrofitManagement.okHttpClient = okHttpClient
    }

    private fun createDefaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true).build()
    }

    private fun formatException(baseException: BaseException): String {
        return when (baseException.realException) {
            is ConnectException, is SocketTimeoutException, is InterruptedIOException -> {
                "连接超时！请检查您的网络设置"
            }
            is UnknownHostException -> {
                "数据获取失败，请检查您的网络"
            }
            null -> {
                //服务器异常
                baseException.errorMessage
            }
            else -> {
                "请求过程抛出异常"
            }
        }
    }

    class Builder constructor(internal val context: Context, internal val serverUrl: String) {

        //用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
        internal var formatExceptionFun: ((baseException: BaseException) -> String)? = null

        internal var okHttClient: OkHttpClient? = null

        fun build(): ReactiveHttp {
            return ReactiveHttp(this)
        }

        fun okHttClient(httpClient: OkHttpClient): Builder {
            okHttClient = httpClient
            return this
        }

        fun formatException(function: (baseException: BaseException) -> String): Builder {
            formatExceptionFun = function
            return this
        }

    }

}