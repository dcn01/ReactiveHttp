package leavesc.reactivehttp.core.config

import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:48
 * 描述：
 */
/**
 * @param errorMessage      服务器返回的异常信息 或者是 运行时异常抛出的信息，是最原始的异常信息
 * @param code              服务器返回的错误码 或者是 HttpConfig.CODE_UNKNOWN（运行时异常）
 * @param realException     用于当 code 是 HttpConfig.CODE_UNKNOWN 时，存储真实的运行时异常
 */
sealed class BaseException(private val errorMessage: String, val code: Int, val realException: Throwable? = null) : Exception(errorMessage) {

    //格式化好的异常信息
    val formatError: String
        get() {
            return when (realException) {
                is ConnectException, is SocketTimeoutException, is InterruptedIOException -> {
                    "连接超时！请检查您的网络设置"
                }
                is UnknownHostException -> {
                    "数据获取失败，请检查您的网络"
                }
                null -> { //服务器异常
                    "服务器异常"
                }
                else -> {
                    "请求失败"
                }
            }
        }

}

//服务器请求成功了，但 status != successCode
class ServerBadException(message: String, code: Int) : BaseException(message, code)

//请求过程抛出异常
class RequestBadException(message: String, code: Int, realException: Throwable) : BaseException(message, code, realException)