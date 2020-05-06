package leavesc.reactivehttp.core.datasource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import leavesc.reactivehttp.core.BuildConfig
import leavesc.reactivehttp.core.RetrofitManagement
import leavesc.reactivehttp.core.callback.BaseRequestCallback
import leavesc.reactivehttp.core.callback.QuietCallback
import leavesc.reactivehttp.core.config.HttpConfig
import leavesc.reactivehttp.core.coroutine.ICoroutineEvent
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.exception.LocalBadException
import leavesc.reactivehttp.core.utils.showToastFun
import leavesc.reactivehttp.core.viewmodel.IUIActionEvent

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:56
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
open class BaseRemoteDataSource<T : Any>(private val iActionEvent: IUIActionEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    //子类通过改变此字段来改为 mock 环境
    protected open val isMockState: Boolean
        get() = false

    protected val mockUrl: String
        get() = RetrofitManagement.mockUrl

    protected val releaseUrl: String
        get() = RetrofitManagement.serverUrl

    //此处逻辑是为了细粒度地控制每个接口对应的 Host
    //1.如果当前是 release 包，为了安全考虑避免开发者在发包时忘记修改 mock 状态，直接返回 releaseUrl
    //2.如果调用接口时有传入 host，则直接返回该 host
    //3.如果当前是 mock 状态，则返回 mock url
    //4.否则最终返回 releaseUrl
    //就是说，如果在 子DataSource 里所有接口都是需要使用 mock 的话，则在 子DataSource 继承 isMockState 将之改为 true
    //如果只是少量接口需要 mock 的话，则使用 getService(mockUrl) 来调用 mock 接口
    private fun generateApiHost(host: String): String {
        if (!BuildConfig.DEBUG) {
            return releaseUrl
        }
        if (host.isNotBlank()) {
            return host
        }
        if (isMockState) {
            return mockUrl
        }
        return releaseUrl
    }

    protected fun getService(host: String = ""): T {
        return RetrofitManagement.getService(serviceApiClass, generateApiHost(host))
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

    protected fun generateBaseExceptionReal(throwable: Throwable): BaseException {
        return generateBaseException(throwable).apply {
            exceptionRecord(this)
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
        showToastFun(msg)
    }

    private fun exceptionRecord(throwable: Throwable) {
        HttpConfig.exceptionRecordFun?.invoke(throwable)
    }

}