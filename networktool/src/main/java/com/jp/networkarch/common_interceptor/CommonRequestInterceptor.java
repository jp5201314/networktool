package com.jp.networkarch.common_interceptor;


import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/20 23:38
 * @Description : 请求拦截器  加入请求头
 */
public class CommonRequestInterceptor implements Interceptor {

    private INetworkRequireInfo iNetworkRequireInfo;
    private HashMap <String,String>hashMap;

    public CommonRequestInterceptor(){
        hashMap = new HashMap<>();
    }
    public CommonRequestInterceptor(INetworkRequireInfo iNetworkRequireInfo){
        this.iNetworkRequireInfo = iNetworkRequireInfo;
        hashMap = iNetworkRequireInfo.getHeaderInfo();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (hashMap!=null&&hashMap.size()>0){
            StringBuilder headerBuilder = new StringBuilder();
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                builder.addHeader(stringStringEntry.getKey(),stringStringEntry.getValue());
                headerBuilder.append("键:"+stringStringEntry.getKey()+"\t值:"+stringStringEntry.getValue()+"\n");
            }
            System.out.println(("加入请求头\t"+headerBuilder.toString()));
        }
        HashMap<String,Object> paramMap = new HashMap<String, Object>();
        StringBuilder bodyBuilder = new StringBuilder();
        Request request = builder.build();
        RequestBody requestBody = request.body();
        if (requestBody instanceof FormBody) {
            if (((FormBody) requestBody).size()>0){
                for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
                    paramMap.put(((FormBody) requestBody).encodedName(i),((FormBody) requestBody).encodedValue(i));
                }
                for (String s : paramMap.keySet()) {
                    bodyBuilder.append(s+":"+ URLDecoder.decode(paramMap.get(s).toString()
                            , "utf-8")+"\n");
                }
                System.out.println("加入请求参数\t"+bodyBuilder.toString());
            }
        }
        return chain.proceed(request);
    }
}
