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

    //此字段用于声明在 BaseViewModel，BaseRemoteDataSource，BaseView 下和生命周期绑定的协程作用域
    //推荐的做法是：
    //1.BaseView 单独声明自己和 View 相关联的作用域
    //2.BaseViewModel 单独声明自己和 ViewModel 相关联的作用域，
    //  因为一个 BaseViewModel 可能和多个 BaseView 相关联，所以不要把 BaseView 的 CoroutineScope 传给 BaseViewModel
    //3.BaseRemoteDataSource 首选使用 BaseViewModel 传过来的 lifecycleCoroutineScope，
    //  因为 BaseRemoteDataSource 和 BaseViewModel 是一对一的关系
    val lifecycleCoroutineScope: CoroutineScope

    //此字段用于声明在全局范围下的协程作用域，不和生命周期绑定
    val globalCoroutineScope: CoroutineScope
        get() = GlobalScope

    val mainDispatcher: CoroutineDispatcher
        get() = Handler(Looper.getMainLooper()).asCoroutineDispatcher()

    private fun defaultLaunch(coroutineScope: CoroutineScope, context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job {
        return coroutineScope.launch(context) {
            block()
        }
    }

    //用于在 UI 线程完成操作
    fun launchUI(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(lifecycleCoroutineScope, mainDispatcher, block)
    }

    //用于完成 CPU 密集型的操作
    fun launchCPU(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(lifecycleCoroutineScope, Dispatchers.Default, block)
    }

    //用于在 IO 密集型的操作
    fun launchIO(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(lifecycleCoroutineScope, Dispatchers.IO, block)
    }

    //用于在 UI 线程完成操作
    fun launchUIGlobal(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(globalCoroutineScope, mainDispatcher, block)
    }

    //用于完成 CPU 密集型的操作
    fun launchCPUGlobal(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(globalCoroutineScope, Dispatchers.Default, block)
    }

    //用于在 IO 密集型的操作
    fun launchIOGlobal(block: suspend CoroutineScope.() -> Unit): Job {
        return defaultLaunch(globalCoroutineScope, Dispatchers.IO, block)
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