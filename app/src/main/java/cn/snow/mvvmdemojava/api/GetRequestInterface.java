package cn.snow.mvvmdemojava.api;

import cn.snow.mvvmdemojava.bean.TestBean;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface GetRequestInterface {
    @GET("get/username")
    Observable<TestBean> getUsername();
}
