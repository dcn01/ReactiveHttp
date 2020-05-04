package leavesc.reactivehttp.weather.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_test.*
import leavesc.reactivehttp.weather.R
import leavesc.reactivehttp.weather.core.view.BaseActivity
import leavesc.reactivehttp.weather.core.viewmodel.TestViewModel

/**
 * 作者：leavesC
 * 时间：2020/5/3 21:38
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
class TestActivity : BaseActivity() {

    private val testViewModel by getViewModel(TestViewModel::class.java) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        btn_test.setOnClickListener {
            testViewModel.testDelay()
        }
        btn_test2.setOnClickListener {
            testViewModel.testPair()
        }
    }

}
