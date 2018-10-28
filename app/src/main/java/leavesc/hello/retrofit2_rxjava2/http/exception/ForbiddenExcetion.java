package leavesc.hello.retrofit2_rxjava2.http.exception;

import leavesc.hello.retrofit2_rxjava2.http.HttpCode;
import leavesc.hello.retrofit2_rxjava2.http.exception.base.BaseException;

/**
 * 作者：叶应是叶
 * 时间：2018/10/25 21:46
 * 描述：
 */
public class ForbiddenExcetion extends BaseException {

    public ForbiddenExcetion() {
        super(HttpCode.CODE_PARAMETER_INVALID, "404错误");
    }

}