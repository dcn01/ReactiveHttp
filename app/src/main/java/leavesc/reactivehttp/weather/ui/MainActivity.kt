package leavesc.reactivehttp.weather.ui

import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_main.*
import leavesc.reactivehttp.weather.R
import leavesc.reactivehttp.weather.core.cache.AreaCache
import leavesc.reactivehttp.weather.core.view.BaseActivity

/**
 * 作者：leavesC
 * 时间：2019/5/31 15:39
 * 描述：
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_weather.setOnClickListener {
            if (TextUtils.isEmpty(AreaCache.getAdCode(this))) {
                startActivity(MapActivity::class.java)
            } else {
                startActivity(WeatherActivity::class.java)
            }
            finish()
        }
        btn_request.setOnClickListener {
            startActivity(TestActivity::class.java)
        }
    }

}