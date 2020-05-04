package leavesc.reactivehttp.core.datasource

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import leavesc.reactivehttp.core.bean.IHttpResBean
import leavesc.reactivehttp.core.callback.RequestCallback
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.exception.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:16
 * 描述：
 */
open class RemoteDataSource<T : Any>(iActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : BaseRemoteDataSource<T>(iActionEvent, serviceApiClass) {

    protected fun <T> execute(callback: RequestCallback<T>?, block: suspend () -> IHttpResBean<T>): Job {
        return execute(callback, showLoading = false, block = block)
    }

    protected fun <T> executeLoading(callback: RequestCallback<T>?, block: suspend () -> IHttpResBean<T>): Job {
        return execute(callback, showLoading = true, block = block)
    }

    private fun <T> execute(callback: RequestCallback<T>?, showLoading: Boolean, block: suspend () -> IHttpResBean<T>): Job {
        return lifecycleSupportedScope.launch(mainDispatcher) {
            try {
                if (showLoading) {
                    showLoading()
                }
                callback?.onStart()
                val response = block()
                callback?.let {
                    if (response.httpIsSuccess) {
                        callback.onSuccess(response.httpData)
                        withIO {
                            callback.onSuccessIO(response.httpData)
                        }
                    } else {
                        throw ServerBadException(response.httpMsg, response.httpCode)
                    }
                }
            } catch (throwable: Throwable) {
                handleException(generateBaseExceptionReal(throwable), callback)
            } finally {
                try {
                    callback?.onFinally()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    //同步请求，可能会抛出异常，外部需做好捕获异常的准备
    @Throws(BaseException::class)
    protected fun <T> request(block: suspend () -> IHttpResBean<T>): T {
        return runBlocking {
            try {
                val asyncIO = asyncIO {
                    block()
                }
                val response = asyncIO.await()
                if (response.httpIsSuccess) {
                    return@runBlocking response.httpData
                }
                throw ServerBadException(response.httpMsg, response.httpCode)
            } catch (throwable: Throwable) {
                throw generateBaseExceptionReal(throwable)
            }
        }
    }

}