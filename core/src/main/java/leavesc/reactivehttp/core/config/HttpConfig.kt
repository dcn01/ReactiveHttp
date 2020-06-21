package leavesc.reactivehttp.core.config

import android.content.Context
import leavesc.reactivehttp.core.exception.BaseException

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:49
 * 描述：
 */
internal object HttpConfig {

    lateinit var context: Context

    lateinit var exceptionFormatFun: (baseException: BaseException) -> String

    var exceptionRecordFun: ((throwable: Throwable) -> Unit)? = null

    var exceptionHandleFun: ((exception: BaseException) -> Boolean)? = null

    lateinit var isReleaseFun: (() -> Boolean)

    //本地定义的 code 以 CODE_LOCAL 开头

    //此变量用于表示在网络请求过程过程中抛出了异常
    const val CODE_LOCAL_UNKNOWN = -1024

}