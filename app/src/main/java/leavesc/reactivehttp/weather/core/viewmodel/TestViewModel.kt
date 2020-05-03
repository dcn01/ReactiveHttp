package leavesc.reactivehttp.weather.core.viewmodel

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import leavesc.reactivehttp.core.callback.RequestMultiplyCallback
import leavesc.reactivehttp.core.exception.BaseException
import leavesc.reactivehttp.core.viewmodel.BaseViewModel
import leavesc.reactivehttp.weather.core.http.TestDataSource

/**
 * 作者：leavesC
 * 时间：2020/5/3 21:39
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
class TestViewModel : BaseViewModel() {

    private fun log(msg: String) {
        Log.e("TestViewModel", msg)
    }

    private val testDataSource = TestDataSource(this)

    private var job: Job? = null

    fun testDelay() {
        job?.cancel(CancellationException("xxxasafafasfa"))
        job = testDataSource.testDelay(object : RequestMultiplyCallback<String> {

            override fun onStart() {
                super.onStart()
//                showLoading()
                log("onStart: " + Thread.currentThread().name)
            }

            override fun onSuccess(data: String) {
                log("onSuccess: " + data)
            }

            override suspend fun onSuccessIO(data: String) {
                super.onSuccessIO(data)
                log("onSuccessIO: " + data)
                repeat(100){
                    log("onSuccessIO: " + it)
                    delay(100)
                }
            }

            override fun onFail(exception: BaseException) {
                log("onFail: " + exception.errorMessage)
            }

            override fun onFinally() {
                super.onFinally()
//                dismissLoading()
                log("onFinally: " + Thread.currentThread().name)
            }

        })
    }

}