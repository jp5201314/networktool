package com.jp.networkarch.common_interceptor;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/20 23:38
 * @Description : 响应拦截器
 */
public class CommonResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        long requestTime = System.currentTimeMillis();
        Request request = chain.request();
        Response response = chain.proceed(request);
        long responseTime = System.currentTimeMillis();
        //请求的时间间隔
        long requestTimeDuring = responseTime-requestTime;
        System.out.println(request.url()+"\n"+"请求耗时:"+requestTimeDuring+"ms");
        ResponseBody responseBody = response.body();
        long responseContentLength = responseBody.contentLength();
        BufferedSource bufferedSource = responseBody.source();
        bufferedSource.request(Integer.MAX_VALUE);
        Buffer buffer =bufferedSource.getBuffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        charset = contentType.charset(charset);
        if (responseContentLength != 0L) {
            Logger.json(buffer.clone().readString(charset));
        }
        return response;
    }
}
