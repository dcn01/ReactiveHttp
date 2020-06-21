package leavesc.reactivehttp.core.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import leavesc.reactivehttp.core.coroutine.ICoroutineEvent
import leavesc.reactivehttp.core.utils.showToastFun

/**
 * 作者：CZY
 * 时间：2020/4/30 15:23
 * 描述：
 */
open class BaseActionEvent

class ShowLoadingEvent(val message: String) : BaseActionEvent()

object DismissLoadingEvent : BaseActionEvent()

object FinishViewEvent : BaseActionEvent()

class ShowToastEvent(val message: String) : BaseActionEvent()

//用于定义 View 和  ViewModel 均需要实现的一些 UI 层行为
interface IUIActionEvent : ICoroutineEvent {

    fun showLoading(msg: String)

    fun showLoading() {
        showLoading("")
    }

    fun dismissLoading()

    fun showToast(msg: String)

    fun finishView()

}

interface IUIActionEventObserver : IUIActionEvent {

    val lContext: Context?

    val lLifecycleOwner: LifecycleOwner

    fun <T : BaseViewModel> getViewModel(clazz: Class<T>, initializer: (T.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): Lazy<T> {
        return lazy {
            getViewModelFast(clazz, initializer)
        }
    }

    private fun <T : BaseViewModel> getViewModelFast(clazz: Class<T>, initializer: (T.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): T {
        return when (val localValue = lLifecycleOwner) {
            is ViewModelStoreOwner -> {
                ViewModelProvider(localValue).get(clazz)
            }
            else -> {
                clazz.newInstance()
            }
        }.apply {
            observerActionEvent()
            initializer?.invoke(this, lLifecycleOwner)
        }
    }

    fun BaseViewModel.observerActionEvent() {
        vmActionEvent.observe(lLifecycleOwner, Observer {
            generateActionEvent(it)
        })
    }

    fun generateActionEvent(baseActionEvent: BaseActionEvent) {
        when (baseActionEvent) {
            is ShowLoadingEvent -> {
                this@IUIActionEventObserver.showLoading(baseActionEvent.message)
            }
            DismissLoadingEvent -> {
                this@IUIActionEventObserver.dismissLoading()
            }
            FinishViewEvent -> {
                this@IUIActionEventObserver.finishView()
            }
            is ShowToastEvent -> {
                this@IUIActionEventObserver.showToast(baseActionEvent.message)
            }
        }
    }

    override fun showToast(msg: String) {
        showToastFun(msg)
    }

    fun <T : Activity> startActivity(clazz: Class<T>) {
        lContext?.apply {
            startActivity(Intent(this, clazz))
        }
    }

}