package leavesc.reactivehttp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import leavesc.reactivehttp.core.config.BaseException
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.config.RequestBadException
import leavesc.reactivehttp.core.config.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IBaseViewModelEvent
import leavesc.reactivehttp.core.viewmodel.ICoroutineEvent

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:16
 * 描述：
 */
open class BaseRemoteDataSource<T : Any>(private val baseViewModelEventEvent: IBaseViewModelEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    protected fun getService(host: String = HttpConfig.BASE_URL_MAP): T {
        return RetrofitManagement.getService(serviceApiClass, host)
    }

    override val lCoroutineScope: CoroutineScope
        get() = baseViewModelEventEvent?.lCoroutineScope ?: GlobalScope

    protected fun <T> execute(block: suspend () -> IBaseResponse<T>, callback: RequestCallback<T>?, quietly: Boolean = false): Job {
        val temp = true
        return launchIO {
            try {
                if (!temp) {
                    launchUI {
                        showLoading()
                    }
                }
                val response = block()
                callback?.let {
                    if (response.isSuccess) {
                        launchUI {
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
                    launchUI {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private fun <T> handleException(throwable: Throwable, callback: RequestCallback<T>?) {
        callback?.let {
            launchUI {
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