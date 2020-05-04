package leavesc.reactivehttp.core.datasource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import leavesc.reactivehttp.core.RetrofitManagement
import leavesc.reactivehttp.core.callback.BaseRequestCallback
import leavesc.reactivehttp.core.callback.QuietCallback
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.coroutine.ICoroutineEvent
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.exception.LocalBadException
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:56
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
open class BaseRemoteDataSource<T : Any>(private val iActionEvent: IUIActionEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    protected fun getService(host: String = RetrofitManagement.serverUrl): T {
        return RetrofitManagement.getService(serviceApiClass, host)
    }

    override val lifecycleSupportedScope: CoroutineScope = iActionEvent?.lifecycleSupportedScope
            ?: GlobalScope

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

    protected suspend fun handleException(exception: BaseException, callback: BaseRequestCallback?) {
        callback?.let {
            withMain {
                when (callback) {
                    is QuietCallback -> {
                        callback.onFail(exception)
                    }
                    else -> {
                        showToast(exception.formatError)
                        callback.onFail(exception)
                    }
                }
            }
        }
    }

    protected fun showLoading() {
        iActionEvent?.showLoading()
    }

    protected fun dismissLoading() {
        iActionEvent?.dismissLoading()
    }

    protected fun showToast(msg: String) {
        iActionEvent?.showToast(msg)
    }

}