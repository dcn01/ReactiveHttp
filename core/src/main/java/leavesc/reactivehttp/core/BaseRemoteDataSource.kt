package leavesc.reactivehttp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
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
open class BaseRemoteDataSource<T : Any>(private val iBaseViewModelEvent: IBaseViewModelEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    protected fun getService(host: String = HttpConfig.BASE_URL_MAP): T {
        return RetrofitManagement.getService(serviceApiClass, host)
    }

    override val lifecycleCoroutineScope: CoroutineScope = iBaseViewModelEvent?.lifecycleCoroutineScope
            ?: GlobalScope

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
                    if (response.httpIsSuccess) {
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

    //同步请求，可能会抛出异常，外部需做好捕获异常的准备
    @Throws(BaseException::class)
    protected fun <T> request(block: suspend () -> IBaseResponse<T>): T {
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

    private fun generateBaseException(throwable: Throwable): BaseException {
        return if (throwable is BaseException) {
            throwable
        } else {
            RequestBadException(throwable.message
                    ?: "", HttpConfig.CODE_LOCAL_UNKNOWN, throwable)
        }
    }

    private fun <T> handleException(throwable: Throwable, callback: RequestCallback<T>?) {
        callback?.let {
            launchUI {
                val exception = generateBaseException(throwable)
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
        iBaseViewModelEvent?.showLoading()
    }

    private fun dismissLoading() {
        iBaseViewModelEvent?.dismissLoading()
    }

    private fun showToast(msg: String) {
        iBaseViewModelEvent?.showToast(msg)
    }

}