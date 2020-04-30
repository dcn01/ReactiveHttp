package leavesc.reactivehttp.core.holder

import android.content.Context
import android.widget.Toast
import leavesc.reactivehttp.core.exception.BaseException

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:07
 * 描述：
 */
internal object ContextHolder {

    lateinit var context: Context

}

internal object HttpActionHolder {

    lateinit var formatExceptionFun: (baseException: BaseException) -> String

}

fun showToast(context: Context = ContextHolder.context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}