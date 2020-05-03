package leavesc.reactivehttp.core.datasource

import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import leavesc.reactivehttp.core.bean.IHttpResBean
import leavesc.reactivehttp.core.callback.RequestTripleCallback
import leavesc.reactivehttp.core.exception.ServerBadException
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:55
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
open class RemoteExtendDataSource<T : Any>(iActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : RemoteDataSource<T>(iActionEvent, serviceApiClass) {

    protected fun <T1, T2, T3> execute(callback: RequestTripleCallback<T1, T2, T3>?, showLoading: Boolean,
                                       block1: suspend () -> IHttpResBean<T1>,
                                       block2: suspend () -> IHttpResBean<T2>,
                                       block3: suspend () -> IHttpResBean<T3>): Job {
        return lifecycleScope.launch(mainDispatcher) {
            try {
                if (showLoading) {
                    showLoading()
                }
                callback?.onStart()
                val async1 = async {
                    block1()
                }
                val async2 = async {
                    block2()
                }
                val async3 = async {
                    block3()
                }
                val response1 = async1.await()
                val response2 = async2.await()
                val response3 = async3.await()
                callback?.let {
                    if (response1.httpIsFailed) {
                        throw ServerBadException(response1.httpMsg, response1.httpCode)
                    }
                    if (response2.httpIsFailed) {
                        throw ServerBadException(response2.httpMsg, response2.httpCode)
                    }
                    if (response3.httpIsFailed) {
                        throw ServerBadException(response3.httpMsg, response3.httpCode)
                    }
                    callback.onSuccess(response1.httpData, response2.httpData, response3.httpData)
                    withIO {
                        callback.onSuccessIO(response1.httpData, response2.httpData, response3.httpData)
                    }
                }
            } catch (throwable: Throwable) {
                Log.e("TAG", "message: " + throwable.message)
                handleException(generateBaseException(throwable), callback)
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