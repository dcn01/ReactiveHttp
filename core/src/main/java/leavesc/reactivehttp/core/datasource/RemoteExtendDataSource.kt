package leavesc.reactivehttp.core.datasource

import kotlinx.coroutines.*
import leavesc.reactivehttp.core.bean.IHttpResBean
import leavesc.reactivehttp.core.callback.BaseRequestCallback
import leavesc.reactivehttp.core.callback.RequestPairCallback
import leavesc.reactivehttp.core.callback.RequestTripleCallback
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.exception.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:55
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
/**
 * 提供了 两个/三个 接口同时并发请求的方法，当所有接口都请求成功时，会通过 onSuccess 方法传出请求结果
 * 当包含的某个接口请求失败时，则会直接回调 onFail 方法
 */
open class RemoteExtendDataSource<T : Any>(iActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : RemoteDataSource<T>(iActionEvent, serviceApiClass) {

    @Throws(BaseException::class)
    private fun executeReal(callback: BaseRequestCallback?, showLoading: Boolean,
                            vararg blockList: suspend () -> IHttpResBean<*>): Deferred<List<IHttpResBean<*>>> {
        return lifecycleSupportedScope.async(mainDispatcher) {
            if (showLoading) {
                showLoading()
            }
            callback?.onStart()
            val responseList = blockList.map { async { it() } }.awaitAll()
            val failed = responseList.find { it.httpIsFailed } ?: return@async responseList
            throw ServerBadException(failed.httpMsg, failed.httpCode)
        }
    }

    protected fun <T1, T2, T3> execute(callback: RequestTripleCallback<T1, T2, T3>?, showLoading: Boolean,
                                       block1: suspend () -> IHttpResBean<T1>,
                                       block2: suspend () -> IHttpResBean<T2>,
                                       block3: suspend () -> IHttpResBean<T3>): Job {
        return lifecycleSupportedScope.launch(mainDispatcher) {
            try {
                val result = executeReal(callback, showLoading, block1, block2, block3)
                val await = result.await()
                callback?.onSuccess(await[0].httpData as T1, await[1].httpData as T2, await[2].httpData as T3)
                withIO {
                    callback?.onSuccessIO(await[0].httpData as T1, await[1].httpData as T2, await[2].httpData as T3)
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

    protected fun <T1, T2> execute(callback: RequestPairCallback<T1, T2>?, showLoading: Boolean,
                                   block1: suspend () -> IHttpResBean<T1>,
                                   block2: suspend () -> IHttpResBean<T2>): Job {
        return lifecycleSupportedScope.launch(mainDispatcher) {
            try {
                val result = executeReal(callback, showLoading, block1, block2)
                val await = result.await()
                callback?.onSuccess(await[0].httpData as T1, await[1].httpData as T2)
                withIO {
                    callback?.onSuccessIO(await[0].httpData as T1, await[1].httpData as T2)
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

}