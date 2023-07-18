package cn.snow.network.okhttpinterceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import cn.snow.network.INetWorkInitInfo;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 利用拦截器添加请求的公共参数
 */
public class CommonRequestInterceptor implements Interceptor {

    INetWorkInitInfo iNetWorkInitInfo;

    public CommonRequestInterceptor(INetWorkInitInfo iNetWorkInitInfo) {
        this.iNetWorkInitInfo = iNetWorkInitInfo;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        long timeRequest = System.currentTimeMillis();
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("systemOs", "Android");
        builder.addHeader("appId", iNetWorkInitInfo.appId());
        builder.addHeader("appVersion", iNetWorkInitInfo.appVersion());
        builder.addHeader("time", String.valueOf(timeRequest));

        return chain.proceed(builder.build());
    }
}
