package cn.snow.network.okhttpinterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CommonNetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request());
    }
}
