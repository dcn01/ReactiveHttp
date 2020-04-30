package leavesc.reactivehttp.core

import android.content.Context
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.exception.BaseException
import okhttp3.OkHttpClient

/**
 * 作者：CZY
 * 时间：2020/4/30 15:29
 * 描述：
 */
class ReactiveHttp internal constructor(builder: Builder) {

    val context = builder.context

    val serverUrl = builder.serverUrl

    val okHttpClient = builder.okHttClient ?: OkHttpClient()

    val formatExceptionFun = builder.formatExceptionFun ?: HttpConfig::formatException

    class Builder constructor(internal val context: Context, internal val serverUrl: String) {

        //用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
        internal var formatExceptionFun: ((baseException: BaseException) -> String)? = null

        internal var okHttClient: OkHttpClient? = null

        fun build(): ReactiveHttp {
            return ReactiveHttp(this)
        }

        fun okHttClient(httpClient: OkHttpClient) {
            okHttClient = httpClient
        }

        fun formatException(function: (baseException: BaseException) -> String) {
            formatExceptionFun = function
        }

    }

}