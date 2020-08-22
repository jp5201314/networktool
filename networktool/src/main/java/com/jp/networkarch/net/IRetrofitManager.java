package com.jp.networkarch.net;
import com.jp.networkarch.common_interceptor.CommonInterceptor;
import com.jp.networkarch.common_interceptor.INetworkRequireInfo;
import com.jp.networkarch.converter.JsonOrXmlConverterFactory;
import com.jp.networkarch.environment.IRuntimeEnvironment;
import com.jp.networkarch.error_handle.HttpErrorHandler;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/20 23:01
 * @Description : RetrofitManager  网络访问管理器
 */
public abstract class IRetrofitManager implements IRuntimeEnvironment{

    private static String baseUrl = "";
    private static INetworkRequireInfo iNetworkRequireInfo;
    private static OkHttpClient okHttpClient;
    private static HashMap<String, Retrofit> hashMap = new HashMap<>();


    public IRetrofitManager(){
    }

    public  void init(INetworkRequireInfo requireInfo,String baseUrl) {
        iNetworkRequireInfo = requireInfo;
        this.baseUrl = baseUrl;
    }

    public static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //.addInterceptor(new CommonResponseInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);
        if (iNetworkRequireInfo!=null){
           // builder.addInterceptor(new CommonRequestInterceptor(iNetworkRequireInfo));
            builder.addInterceptor(new CommonInterceptor(iNetworkRequireInfo));
        }else {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        okHttpClient =  builder.build();
        return okHttpClient;
    }

    public static Retrofit getRetrofit(Class serviceClass) {
        Retrofit retrofit = hashMap.get(baseUrl + serviceClass.getName());
        if (retrofit != null) {
            return retrofit;
        }
        //构建者模式
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        Retrofit configRetrofit = retrofitBuilder.baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(JsonOrXmlConverterFactory.Companion.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        hashMap.put(baseUrl + serviceClass.getName(), configRetrofit);
        return configRetrofit;
    }


    /**
     * 配置网络切换和错误处理
     * @param observer
     * @param <T>
     * @return
     */
    public   <T> ObservableTransformer<T, T> applySchedulers(Observer<T> observer) {
        return upstream -> {
            Observable<T> observable = upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(getAppErrorHandle())
            .onErrorResumeNext(new HttpErrorHandler<T>());
            observable.subscribe(observer);
            return observable;
        };
    }

    protected abstract Interceptor addInterceptor();
    protected abstract <T>Function<T,T>getAppErrorHandle();
}
