package leavesc.reactivehttp.core.callback

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:47
 * 描述：
 */
//使用以下回调，在请求失败时会自动 Toast 失败原因
interface RequestCallback<T> : BaseRequestCallback {

    //在 onFinally 方法之前执行，当执行完毕后再调用 onFinally 方法
    //Main 线程调用
    fun onSuccess(data: T) {

    }

    //在 onFinally 方法之前执行，当执行完毕后再调用 onFinally 方法
    //考虑到网络请求成功后有需要将数据保存到数据库的需求，所以此方法会在 IO 线程进行调用
    //注意外部不要在此处另开子线程
    suspend fun onSuccessIO(data: T) {

    }

}

//使用以下回调，在请求失败时不会 Toast 失败原因
interface RequestQuietCallback<T> : RequestCallback<T>, QuietCallback