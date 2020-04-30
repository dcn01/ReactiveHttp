package leavesc.reactivehttp.core.exception

import leavesc.reactivehttp.core.config.HttpConfig

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:48
 * 描述：
 */
/**
 * @param errorMessage      服务器返回的异常信息 或者是 请求过程中抛出的信息，是最原始的异常信息
 * @param code              服务器返回的错误码 或者是 HttpConfig 中定义的本地错误码
 * @param realException     用于当 code 是本地错误码时，存储真实的运行时异常
 */
sealed class BaseException(val errorMessage: String, val code: Int, val realException: Throwable?) : Exception(errorMessage) {

    //是否是由于服务器返回的 code != successCode 导致的失败
    val isServerError: Boolean
        get() = realException == null

    //格式化好的异常信息
    val formatError: String
        get() = HttpConfig.formatExceptionFun(this)

}

//服务器请求成功了，但 status != successCode
class ServerBadException(message: String, code: Int) : BaseException(message, code, null)

//请求过程抛出异常
class LocalBadException(message: String, code: Int, realException: Throwable) : BaseException(message, code, realException)