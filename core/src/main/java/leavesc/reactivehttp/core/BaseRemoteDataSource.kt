package leavesc.reactivehttp.core

import kotlinx.coroutines.*
import leavesc.reactivehttp.core.bean.IHttpResBean
import leavesc.reactivehttp.core.callback.RequestCallback
import leavesc.reactivehttp.core.callback.RequestMultiplyCallback
import leavesc.reactivehttp.core.callback.RequestMultiplyToastCallback
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.coroutine.ICoroutineEvent
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.exception.LocalBadException
import leavesc.reactivehttp.core.exception.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:16
 * 描述：
 */
open class BaseRemoteDataSource<T : Any>(private val iActionEvent: IUIActionEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    protected fun getService(host: String = RetrofitManagement.serverUrl): T {
        return RetrofitManagement.getService(serviceApiClass, host)
    }

    override val lifecycleScope: CoroutineScope = iActionEvent?.lifecycleScope
            ?: GlobalScope

    protected fun <T> execute(callback: RequestCallback<T>?, showLoading: Boolean = false, block: suspend () -> IHttpResBean<T>): Job {
        return lifecycleScope.launch(mainDispatcher) {
            val showLoadingTemp = showLoading
            try {
                if (showLoadingTemp) {
                    showLoading()
                }
                callback?.onStart()
                val response = block()
                callback?.let {
                    if (response.httpIsSuccess) {
                        callback.onSuccess(response.httpData)
                    } else {
                        throw ServerBadException(response.httpMsg, response.httpCode)
                    }
                }
            } catch (throwable: Throwable) {
                handleException(generateBaseException(throwable), callback)
            } finally {
                callback?.onFinally()
                if (showLoadingTemp) {
                    dismissLoading()
                }
            }
        }
    }

    //同步请求，可能会抛出异常，外部需做好捕获异常的准备
    @Throws(BaseException::class)
    protected fun <T> request(block: suspend () -> IHttpResBean<T>): T {
        return runBlocking {
            val asyncIO = asyncIO {
                block()
            }
            try {
                val response = asyncIO.await()
                if (response.httpIsSuccess) {
                    return@runBlocking response.httpData
                }
                throw ServerBadException(response.httpMsg, response.httpCode)
            } catch (throwable: Throwable) {
                throw generateBaseException(throwable)
            }
        }
    }

    /**
     * 如果外部想要对 Throwable 进行特殊处理，则可以重写此方法，用于改变异常类型
     * 例如，在 token 失效时接口一般是会返回特定一个 httpCode 用于表明移动端需要去更新 token 了
     * 此时外部就可以实现一个 BaseException 的子类 TokenInvalidException 并在此处返回
     * 从而做到接口异常原因强提醒的效果，而不用去纠结 httpCode 到底是多少
     */
    protected open fun generateBaseException(throwable: Throwable): BaseException {
        return if (throwable is BaseException) {
            throwable
        } else {
            LocalBadException(throwable.message
                    ?: "", HttpConfig.CODE_LOCAL_UNKNOWN, throwable)
        }
    }

    private suspend fun <T> handleException(exception: BaseException, callback: RequestCallback<T>?) {
        callback?.let {
            withMain {
                when (callback) {
                    is RequestMultiplyToastCallback -> {
                        showToast(exception.formatError)
                        callback.onFail(exception)
                    }
                    is RequestMultiplyCallback -> {
                        callback.onFail(exception)
                    }
                    else -> {
                        showToast(exception.formatError)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        iActionEvent?.showLoading()
    }

    private fun dismissLoading() {
        iActionEvent?.dismissLoading()
    }

    private fun showToast(msg: String) {
        iActionEvent?.showToast(msg)
    }

}