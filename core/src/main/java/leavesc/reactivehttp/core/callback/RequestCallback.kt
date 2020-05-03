package leavesc.reactivehttp.core.callback

import leavesc.reactivehttp.core.exception.BaseException

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:47
 * 描述：
 */
//如果你不想处理请求接口失败的回调，那在请求接口时使用以下回调，在请求失败时会自动 Toast 失败原因
interface RequestCallback<T> {

    //在显示 Loading 之后且开始网络请求之前执行
    fun onStart() {

    }

    //在 onFinally 方法之前执行，当执行完毕后再调用 onFinally 方法
    //Main 线程调用
    fun onSuccess(data: T) {

    }

    //在 onFinally 方法之前执行，当执行完毕后再调用 onFinally 方法
    //考虑到网络请求成功后有需要将数据保存到数据库的需求，所以此方法会在 IO 线程进行调用
    //注意外部不要在此处另开子线程
    suspend fun onSuccessIO(data: T) {

    }

    //在网络请求结束之后（不管请求成功与否）且隐藏 Loading 之前执行
    fun onFinally() {

    }

}

//如果你想处理请求接口失败的回调，那在请求接口时使用以下两个回调
//区别在于 RequestMultiplyToastCallback 会在请求失败时会自动 Toast 失败原因，而 RequestMultiplyCallback 不会

interface RequestMultiplyCallback<T> : RequestCallback<T> {

    fun onFail(exception: BaseException)

}

interface RequestMultiplyToastCallback<T> : RequestMultiplyCallback<T>