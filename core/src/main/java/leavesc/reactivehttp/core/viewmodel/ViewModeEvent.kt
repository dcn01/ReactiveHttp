package leavesc.reactivehttp.core.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 * 作者：leavesC
 * 时间：2019/5/31 9:38
 * 描述：
 */
open class BaseEvent(open val action: Int)

class BaseViewModelEvent(override val action: Int) : BaseEvent(action) {

    companion object {

        const val SHOW_LOADING_DIALOG = 1

        const val DISMISS_LOADING_DIALOG = 2

        const val SHOW_TOAST = 3

        const val FINISH = 4

    }

    var message: String = ""

}

interface ICoroutineEvent {

    val lCoroutineScope: CoroutineScope

    val mainDispatcher: CoroutineDispatcher
        get() = Handler(Looper.getMainLooper()).asCoroutineDispatcher()

    private fun defaultLaunch(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job {
        return lCoroutineScope.launch(context) {
            block()
        }
    }

    //用于在主线程中启动协程完成操作
    fun launchUI(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(Handler(Looper.getMainLooper()).asCoroutineDispatcher(), block)
    }

    //用于完成 CPU 密集型的操作
    fun launchCPU(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(Dispatchers.Default, block)
    }

    //用于在 IO 密集型的操作
    fun launchIO(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(Dispatchers.IO, block)
    }

}

interface IBaseViewModelEvent : ICoroutineEvent {

    fun showLoading(msg: String)

    fun showLoading() {
        showLoading("")
    }

    fun dismissLoading()

    fun showToast(msg: String)

    fun finishView()

}

interface IBaseViewModelEventObserver : IBaseViewModelEvent {

    val lContext: Context?

    val lLifecycleOwner: LifecycleOwner

    fun initViewModel(): BaseViewModel? {
        return null
    }

    fun initViewModelList(): MutableList<BaseViewModel>? {
        return null
    }

    fun initViewModelEvent() {
        val initViewModelList = initViewModelList()
        if (initViewModelList.isNullOrEmpty()) {
            initViewModel()?.let {
                observeEvent(it)
            }
        } else {
            observeEventList(initViewModelList)
        }
    }

    private fun observeEvent(baseViewModel: BaseViewModel) {
        baseViewModel.baseActionEvent.observe(lLifecycleOwner, Observer { it ->
            it?.let {
                when (it.action) {
                    BaseViewModelEvent.SHOW_LOADING_DIALOG -> {
                        showLoading(it.message)
                    }
                    BaseViewModelEvent.DISMISS_LOADING_DIALOG -> {
                        dismissLoading()
                    }
                    BaseViewModelEvent.SHOW_TOAST -> {
                        showToast(it.message)
                    }
                    BaseViewModelEvent.FINISH -> {
                        finishView()
                    }
                }
            }
        })
    }

    private fun observeEventList(viewModelList: MutableList<BaseViewModel>) {
        for (viewModel in viewModelList) {
            observeEvent(viewModel)
        }
    }

    override fun showToast(msg: String) {
        lContext?.let {
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun <T> startActivity(clazz: Class<T>) {
        lContext?.apply {
            startActivity(Intent(this, clazz))
        }
    }

}