package cn.snow.mvvmdemojava.base;

import cn.snow.mvvmdemojava.BuildConfig;
import cn.snow.network.INetWorkInitInfo;

public class NetWorkInitInfo implements INetWorkInitInfo {


    @Override
    public String appId() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    public String appVersion() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
