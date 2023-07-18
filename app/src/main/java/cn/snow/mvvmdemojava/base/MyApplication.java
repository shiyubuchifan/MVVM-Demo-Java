package cn.snow.mvvmdemojava.base;

import android.app.Application;

import cn.snow.mvvmdemojava.BuildConfig;
import cn.snow.network.NetWorkApi;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        NetWorkApi.init(new NetWorkInitInfo());
    }
}
