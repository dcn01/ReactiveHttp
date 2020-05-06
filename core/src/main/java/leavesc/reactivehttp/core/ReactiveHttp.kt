package leavesc.reactivehttp.core

import android.content.Context
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.exception.BaseException
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
class ReactiveHttp private constructor(builder: Builder) {

    private val context = builder.context.applicationContext

    private val formatExceptionFun = builder.formatExceptionFun ?: ::formatException

    private val exceptionRecordFun = builder.exceptionRecordFun

    private val serverUrl = builder.serverUrl

    private val mockUrl = builder.mockUrl

    private val okHttpClient = builder.okHttClient ?: createDefaultOkHttpClient()

    fun init() {
        HttpConfig.context = context
        HttpConfig.formatExceptionFun = formatExceptionFun
        HttpConfig.exceptionRecordFun = exceptionRecordFun
        RetrofitManagement.serverUrl = serverUrl
        RetrofitManagement.mockUrl = mockUrl
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
        return when (baseException.localException) {
            null -> {
                //接口返回的 httpCode 并非 successCode，直接返回服务器返回的 errorMessage
                baseException.errorMessage
            }
            is ConnectException, is SocketTimeoutException, is InterruptedIOException, is UnknownHostException -> {
                "连接超时！请检查您的网络设置"
            }
            else -> {
                "请求过程抛出异常：" + baseException.errorMessage
            }
        }
    }

    class Builder constructor(internal val context: Context, internal val serverUrl: String) {

        //用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
        internal var formatExceptionFun: ((baseException: BaseException) -> String)? = null

        //用于将网络请求过程中的异常反馈给外部，以便记录
        internal var exceptionRecordFun: ((throwable: Throwable) -> Unit)? = null

        internal var okHttClient: OkHttpClient? = null

        internal var mockUrl: String = ""

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

        fun exceptionRecordFun(function: (throwable: Throwable) -> Unit): Builder {
            exceptionRecordFun = function
            return this
        }

        fun mockUrl(tepMockUrl: String): Builder {
            mockUrl = tepMockUrl
            return this
        }

    }

}