package leavesc.reactivehttp.core

import com.google.gson.annotations.SerializedName
import leavesc.reactivehttp.core.config.HttpConfig

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:58
 * 描述：
 */
class BaseResponse<T>(
        @SerializedName("status") var code: Int = 0,
        @SerializedName("info") var message: String? = null,
        @SerializedName("districts", alternate = ["forecasts"]) var data: T) {

    val isSuccess: Boolean
        get() = code == HttpConfig.CODE_SUCCESS || message == "OK"

}

class OptionT<T>(val value: T)