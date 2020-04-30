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

    override val lifecycleCoroutineScope: CoroutineScope = GlobalScope

    val baseActionEvent = MutableLiveData<BaseViewModelEvent>()

    override fun showLoading(msg: String) {
        val event = BaseViewModelEvent(BaseViewModelEvent.SHOW_LOADING_DIALOG)
        event.message = msg
        baseActionEvent.value = event
    }

    override fun dismissLoading() {
        val event = BaseViewModelEvent(BaseViewModelEvent.DISMISS_LOADING_DIALOG)
        baseActionEvent.value = event
    }

    override fun showToast(msg: String) {
        val event = BaseViewModelEvent(BaseViewModelEvent.SHOW_TOAST)
        event.message = msg
        baseActionEvent.value = event
    }

    override fun finishView() {
        val event = BaseViewModelEvent(BaseViewModelEvent.FINISH)
        baseActionEvent.value = event
    }

}