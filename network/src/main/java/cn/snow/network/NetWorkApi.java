package cn.snow.network;

import java.util.HashMap;

import cn.snow.network.okhttpinterceptor.CommonNetworkInterceptor;
import cn.snow.network.okhttpinterceptor.CommonRequestInterceptor;
import cn.snow.network.okhttpinterceptor.CommonResponseInterceptor;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//网络请求类
public class NetWorkApi {

    private static INetWorkInitInfo iNetWorkInitInfo;

    static HashMap<String, Retrofit> retrofitHashMap = new HashMap<>();

    private static String baseUrl = "https://www.shiyubuchifan.xyz";

    //初始化
    public static void init(INetWorkInitInfo iNetWorkInitInfo) {
        NetWorkApi.iNetWorkInitInfo = iNetWorkInitInfo;
    }

    public static Retrofit getNetWorkAPI(Class c) {
        if (retrofitHashMap.get(baseUrl + c.getName()) != null) {
            return retrofitHashMap.get(baseUrl + c.getName());
        }
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(baseUrl);
        retrofitBuilder.client(getOkHttpClient());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        Retrofit retrofit = retrofitBuilder.build();
        retrofitHashMap.put(baseUrl + c.getName(), retrofit);
        return retrofit;

    }

    public static <T> T getService(Class<T> c) {
        return getNetWorkAPI(c).create(c);
    }

    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        //添加请求返回拦截器
        okHttpClientBuilder.addInterceptor(new CommonRequestInterceptor(iNetWorkInitInfo));
        okHttpClientBuilder.addInterceptor(new CommonResponseInterceptor());
        okHttpClientBuilder.addNetworkInterceptor(new CommonNetworkInterceptor());

        //是Debug模式 打印日志
        if (iNetWorkInitInfo != null && iNetWorkInitInfo.isDebug()) {
            //添加日志拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }

        return okHttpClientBuilder.build();

    }

    public static <T> ObservableTransformer<T, T> oppsObservable(final Observer<T> observer) {
        return upstream -> {
            Observable<T> observable = upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(observer);
            return observable;
        };
    }

    public static <T> ObservableTransformer<T, T> oppsObservableAndHandleError(final Observer<T> observer) {
        return upstream -> {
            Observable<T> observable = upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .map(NetWorkApi.<T>getNetWorkErrorHandle())
                    .onErrorResumeNext(new HttpErrorHandler<T>());
            observable.subscribe(observer);
            return observable;
        };
    }

    public static <T> Function<T, T> getNetWorkErrorHandle() {
        return response -> {
            if (response instanceof Response && ((Response) response).code() != 200) {
                ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();
                exception.code = ((Response) response).code();
                exception.message = ((Response) response).message() != null ? ((Response) response).message() : "";
                throw exception;
            }
            return response;
        };
    }
}
