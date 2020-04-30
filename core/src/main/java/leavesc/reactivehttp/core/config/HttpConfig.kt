package leavesc.reactivehttp.core.config

import leavesc.reactivehttp.core.exception.BaseException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:49
 * 描述：
 */
object HttpConfig {

    const val BASE_URL_MAP = "https://restapi.amap.com/v3/"

    const val KEY = "key"

    const val KEY_MAP = "fb0a1b0d89f3b93adca639f0a29dbf23"

    //服务端返回的 code 以 CODE_SERVER 开头
    const val CODE_SERVER_SUCCESS = 1

    //本地定义的 code 以 CODE_LOCAL 开头，用于定义比如无网络、请求超时等各种异常情况
    const val CODE_LOCAL_UNKNOWN = -1024

    fun formatException(baseException: BaseException): String {
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

}