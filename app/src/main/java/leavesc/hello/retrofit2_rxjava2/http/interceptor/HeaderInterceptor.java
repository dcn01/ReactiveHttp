package leavesc.hello.retrofit2_rxjava2.http.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：叶应是叶
 * 时间：2018/10/27 7:34
 * 描述：
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(originalRequest.method(), originalRequest.body());
        requestBuilder.addHeader("Token", "leavesC");
        return chain.proceed(requestBuilder.build());
    }

}
