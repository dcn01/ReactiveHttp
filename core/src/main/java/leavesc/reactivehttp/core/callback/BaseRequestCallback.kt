package leavesc.reactivehttp.core.callback

import leavesc.reactivehttp.core.exception.BaseException

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:44
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
interface BaseRequestCallback {

    //在显示 Loading 之后且开始网络请求之前执行
    fun onStart() {

    }

    fun onFail(exception: BaseException) {

    }

    //在网络请求结束之后（不管请求成功与否）且隐藏 Loading 之前执行
    fun onFinally() {

    }

}

//继承了此接口，则表明该 callback 在网络请求失败时不需要 Toast 失败原因
interface QuietCallback