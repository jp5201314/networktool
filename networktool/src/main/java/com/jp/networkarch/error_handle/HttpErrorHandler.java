package com.jp.networkarch.error_handle;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/21 22:28
 * @Description : 文件描述
 */
public class HttpErrorHandler<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandler.handleException(throwable));
    }
}
