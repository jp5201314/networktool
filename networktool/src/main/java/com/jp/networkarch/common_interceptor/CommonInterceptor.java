package com.jp.networkarch.common_interceptor;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/22 12:45
 * @Description : 公有的拦截器
 */
public class CommonInterceptor implements Interceptor {
    private INetworkRequireInfo iNetworkRequireInfo;
    private HashMap<String, String> hashMap;

    public CommonInterceptor() {
        hashMap = new HashMap<>();
    }

    public CommonInterceptor(INetworkRequireInfo iNetworkRequireInfo) {
        this.iNetworkRequireInfo = iNetworkRequireInfo;
        hashMap = iNetworkRequireInfo.getHeaderInfo();
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        StringBuilder headerBuilder = new StringBuilder();
        if (hashMap != null && hashMap.size() > 0) {
            for (Map.Entry<String, String> stringStringEntry : hashMap.entrySet()) {
                builder.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                headerBuilder.append("键:" + stringStringEntry.getKey() + "\t值:" + stringStringEntry.getValue() + "\n");
            }
        }
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        StringBuilder bodyBuilder = new StringBuilder();
        Request request = builder.build();
        RequestBody requestBody = request.body();
        if (requestBody instanceof FormBody) {
            if (((FormBody) requestBody).size() > 0) {
                for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
                    paramMap.put(((FormBody) requestBody).encodedName(i), ((FormBody) requestBody).encodedValue(i));
                }
                for (String s : paramMap.keySet()) {
                    bodyBuilder.append(s + ":" + URLDecoder.decode(paramMap.get(s).toString()
                            , "utf-8") + "\n");
                }
            }
        }
        long requestTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long responseTime = System.currentTimeMillis();
        //请求的时间间隔
        long requestTimeDuring = responseTime - requestTime;
        System.out.println("请求地址：" + request.url());
        System.out.println(("加入请求头：" + headerBuilder.toString()));
        System.out.println("加入请求参数：" + bodyBuilder.toString());
        System.out.println("请求耗时：" + requestTimeDuring + "ms");
        ResponseBody responseBody = response.body();
        long responseContentLength = responseBody.contentLength();
        BufferedSource bufferedSource = responseBody.source();
        bufferedSource.request(Integer.MAX_VALUE);
        Buffer buffer = bufferedSource.getBuffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        charset = contentType.charset(charset);
        if (responseContentLength != 0L) {
            String responseStr = buffer.clone().readString(charset);
            System.out.println(responseStr);
            if(isJson(responseStr)){
                Logger.json(responseStr);
            }else if (isXmlDocument(responseStr)){
                Logger.xml(responseStr);
            }
        }
        return chain.proceed(request);
    }

    /**
     * 判断是否是json结构
     */
    public static boolean isJson(String value) {
        try {
            new JSONObject(value);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是xml结构
     */
    private static boolean isXmlDocument(String rtnMsg) {
        boolean flag = true;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(rtnMsg)));
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


}
