package leavesc.reactivehttp.core.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import leavesc.reactivehttp.core.config.HttpConfig

/**
 * 作者：CZY
 * 时间：2020/5/6 11:16
 * 描述：
 */
fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}

private val mainHandler by lazy {
    Handler(Looper.getMainLooper())
}

private fun showToast(msg: String) {
    Toast.makeText(HttpConfig.context, msg, Toast.LENGTH_SHORT).show()
}

fun showToastFun(msg: String) {
    if (msg.isBlank()) {
        return
    }
    if (isMainThread()) {
        showToast(msg)
    } else {
        mainHandler.post {
            showToast(msg)
        }
    }
}