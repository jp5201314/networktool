package com.jp.networkarch.observer;
import com.jp.networkarch.error_handle.ExceptionHandler;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/22 00:13
 * @Description : 模版方法模式
 */
public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
        addDisposable(d);
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailure((ExceptionHandler.ResponseThrowable) e);
    }

    @Override
    public void onComplete() {
    }
    public abstract void addDisposable(Disposable disposable);
    public abstract void onSuccess(T t);
    public abstract void onFailure(ExceptionHandler.ResponseThrowable throwable);

}
