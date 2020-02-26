package leavesc.reactivehttp.core.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * 作者：leavesC
 * 时间：2019/5/31 9:39
 * 描述：
 */
open class BaseViewModel : ViewModel(), IBaseViewModelEvent {

    //lViewModelScope 用于决定在 ViewModel 内部启动的协程的生命周期
    //默认实现是以 ViewModel 的消亡作为所有协程的终结点
    //子 ViewModel 可以覆写该字段以实现新的生命周期
    override val lifecycleCoroutineScope: CoroutineScope = GlobalScope

    val baseActionEvent = MutableLiveData<BaseViewModelEvent>()

    override fun showLoading(msg: String) {
        val event =
                BaseViewModelEvent(BaseViewModelEvent.SHOW_LOADING_DIALOG)
        event.message = msg
        baseActionEvent.value = event
    }

    override fun dismissLoading() {
        val event =
                BaseViewModelEvent(BaseViewModelEvent.DISMISS_LOADING_DIALOG)
        baseActionEvent.value = event
    }

    override fun showToast(msg: String) {
        val event =
                BaseViewModelEvent(BaseViewModelEvent.SHOW_TOAST)
        event.message = msg
        baseActionEvent.value = event
    }

    override fun finishView() {
        val event =
                BaseViewModelEvent(BaseViewModelEvent.FINISH)
        baseActionEvent.value = event
    }

}