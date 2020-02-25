package leavesc.reactivehttp.core

import com.google.gson.annotations.SerializedName
import leavesc.reactivehttp.core.config.HttpConfig

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:58
 * 描述：
 */
interface IBaseResponse<T> {

    val httpCode: Int

    val httpMsg: String

    val httpData: T

    val isSuccess: Boolean

}

class BaseResponse<T>(
        @SerializedName("status") var code: Int = 0,
        @SerializedName("info") var message: String? = null,
        @SerializedName("districts", alternate = ["forecasts"]) var data: T) : IBaseResponse<T> {

    override val httpCode: Int
        get() = code

    override val httpMsg: String
        get() = message ?: ""

    override val httpData: T
        get() = data

    override val isSuccess: Boolean
        get() = code == HttpConfig.CODE_SUCCESS || message == "OK"

}