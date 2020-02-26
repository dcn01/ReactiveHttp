package leavesc.reactivehttp.core.holder

import android.content.Context
import android.widget.Toast

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:07
 * 描述：
 */
object ContextHolder {

    lateinit var context: Context

}

fun showToast(context: Context = ContextHolder.context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}