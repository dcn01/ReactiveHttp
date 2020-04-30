package leavesc.reactivehttp.core.coroutine

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 * 作者：CZY
 * 时间：2020/4/30 15:25
 * 描述：
 */
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

    private fun <T> defaultAsync(coroutineScope: CoroutineScope, context: CoroutineContext, block: suspend CoroutineScope.() -> T): Deferred<T> {
        return coroutineScope.async(context) {
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
    fun <T> asyncUI(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(lifecycleCoroutineScope, mainDispatcher, block)
    }

    //用于完成 CPU 密集型的操作
    fun <T> asyncCPU(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(lifecycleCoroutineScope, Dispatchers.Default, block)
    }

    //用于在 IO 密集型的操作
    fun <T> asyncIO(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(lifecycleCoroutineScope, Dispatchers.IO, block)
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

    //用于在 UI 线程完成操作
    fun <T> asyncUIGlobal(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(globalCoroutineScope, mainDispatcher, block)
    }

    //用于完成 CPU 密集型的操作
    fun <T> asyncCPUGlobal(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(globalCoroutineScope, Dispatchers.Default, block)
    }

    //用于在 IO 密集型的操作
    fun <T> asyncIOGlobal(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return defaultAsync(globalCoroutineScope, Dispatchers.IO, block)
    }

}