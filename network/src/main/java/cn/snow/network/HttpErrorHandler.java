package cn.snow.network;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public  Observable<T> apply(Throwable throwable) throws Throwable {
        return Observable.error(ExceptionHandle.handleServerException(throwable));
    }
}
