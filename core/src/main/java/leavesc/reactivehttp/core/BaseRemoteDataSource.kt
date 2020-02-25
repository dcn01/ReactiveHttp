package leavesc.reactivehttp.core

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import kotlinx.coroutines.android.asCoroutineDispatcher
import leavesc.reactivehttp.core.config.BaseException
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.config.RequestBadException
import leavesc.reactivehttp.core.config.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IBaseViewModeEventScope

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:16
 * 描述：
 */
open class BaseRemoteDataSource<T : Any>(private val baseViewModelEventEvent: IBaseViewModeEventScope?, private val serviceApiClass: Class<T>) {

    protected fun getService(host: String = HttpConfig.BASE_URL_MAP): T {
        return RetrofitManagement.getService(serviceApiClass, host)
    }

    protected val scope
        get() = baseViewModelEventEvent?.lViewModelScope ?: GlobalScope

    protected fun <T> execute(block: suspend () -> IBaseResponse<T>, callback: RequestCallback<T>?, quietly: Boolean = false): Job {
        val temp = true
        return scope.launch(Dispatchers.IO) {
            try {
                if (!temp) {
                    withContext(Handler(Looper.getMainLooper()).asCoroutineDispatcher()) {
                        showLoading()
                    }
                }
                val response = block()
                callback?.let {
                    if (response.isSuccess) {
                        withContext(Handler(Looper.getMainLooper()).asCoroutineDispatcher()) {
                            callback.onSuccess(response.httpData)
                        }
                    } else {
                        handleException(ServerBadException(response.httpMsg, response.httpCode), callback)
                    }
                }
            } catch (throwable: Throwable) {
                handleException(throwable, callback)
            } finally {
                if (!temp) {
                    withContext(Handler(Looper.getMainLooper()).asCoroutineDispatcher()) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <T> handleException(throwable: Throwable, callback: RequestCallback<T>?) {
        callback?.let {
            withContext(Handler(Looper.getMainLooper()).asCoroutineDispatcher()) {
                val exception = if (throwable is BaseException) {
                    throwable
                } else {
                    RequestBadException(throwable.message
                            ?: "", HttpConfig.CODE_LOCAL_UNKNOWN, throwable)
                }
                when (callback) {
                    is RequestMultiplyToastCallback -> {
                        showToast(exception.formatError)
                        if (it is BaseException) {
                            callback.onFail(it)
                        } else {
                            callback.onFail(exception)
                        }
                    }
                    is RequestMultiplyCallback -> {
                        if (it is BaseException) {
                            callback.onFail(it)
                        } else {
                            callback.onFail(exception)
                        }
                    }
                    else -> {
                        showToast(exception.formatError)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        baseViewModelEventEvent?.showLoading()
    }

    private fun dismissLoading() {
        baseViewModelEventEvent?.dismissLoading()
    }

    private fun showToast(msg: String) {
        baseViewModelEventEvent?.showToast(msg)
    }

}