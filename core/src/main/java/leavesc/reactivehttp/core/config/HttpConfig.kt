package leavesc.reactivehttp.core.config

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:49
 * 描述：
 */
internal object HttpConfig {

    const val BASE_URL_MAP = "https://restapi.amap.com/v3/"

    const val KEY = "key"



    //服务端返回的 code 以 CODE_SERVER 开头
    const val CODE_SERVER_SUCCESS = 1

    //本地定义的 code 以 CODE_LOCAL 开头，用于定义比如无网络、请求超时等各种异常情况
    const val CODE_LOCAL_UNKNOWN = -1024

}