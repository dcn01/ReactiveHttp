package leavesc.reactivehttp.weather.core.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import leavesc.reactivehttp.core.viewmodel.BaseViewModel
import leavesc.reactivehttp.core.viewmodel.IBaseViewModelEventObserver

/**
 * 作者：leavesC
 * 时间：2019/5/31 9:36
 * 描述：
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), IBaseViewModelEventObserver {

    override val lContext: Context?
        get() = this

    override val lLifecycleOwner: LifecycleOwner
        get() = this

    private var loadDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModelEvent()
    }

    override fun showLoading(msg: String) {
        if (loadDialog == null) {
            loadDialog = ProgressDialog(lContext)
            loadDialog!!.setCancelable(false)
            loadDialog!!.setCanceledOnTouchOutside(false)
        }
        loadDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun dismissLoading() {
        loadDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun finishView() {
        finish()
    }

    fun <T : BaseViewModel> getViewModel(clazz: Class<T>) =
            ViewModelProviders.of(this).get(clazz)

}