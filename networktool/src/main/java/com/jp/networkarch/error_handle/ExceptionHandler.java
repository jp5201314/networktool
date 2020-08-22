package com.jp.networkarch.error_handle;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import java.net.ConnectException;
import java.text.ParseException;
import javax.net.ssl.SSLHandshakeException;
import retrofit2.HttpException;

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/21 22:31
 * @Description : 异常处理
 */
public class ExceptionHandler {
    //未初始化
    private static final int UNINITIALIZED = 401;
    //禁止
    private static final int FORBIDDEN = 403;
    //未发现
    private static final int NOT_FOUND = 404;
    //超时
    private static final int RESULT_TIMEOUT = 408;
    //服务器错误
    private static final int INTERNET_SERVER_ERROR = 500;
    //错误的网关
    private static final int BAD_GATEWAY = 502;
    //暂停服务
    private static final int SERVICE_UNAVAILABLE = 503;
    //网关超时
    private static final int GATEWAY_TIMEOUT = 504;


    public static Throwable handleException(Throwable throwable) {
        ResponseThrowable rThrowable;
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            rThrowable = new ResponseThrowable(throwable, Error.HTTP_ERROR);
            switch (httpException.code()) {
                case UNINITIALIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case RESULT_TIMEOUT:
                case INTERNET_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                case GATEWAY_TIMEOUT:
                default:
                    rThrowable.errorMsg = "网络错误";
                    break;
            }
            return rThrowable;
        } else if (throwable instanceof ServerException) {
            ServerException serverException = (ServerException) throwable;
            rThrowable = new ResponseThrowable(serverException, serverException.errorCode);
            rThrowable.errorMsg = serverException.errorMsg;
            return rThrowable;
        } else if (throwable instanceof JsonParseException ||
                throwable instanceof JSONException ||
                throwable instanceof ParseException||
                throwable instanceof MalformedJsonException) {
            rThrowable = new ResponseThrowable(throwable, Error.PARSE_ERROR);
            rThrowable.errorMsg = "解析错误";
            return rThrowable;
        } else if (throwable instanceof ConnectException) {
            rThrowable = new ResponseThrowable(throwable, Error.NETWORK_ERROR);
            rThrowable.errorMsg = "连接失败";
            return rThrowable;
        } else if (throwable instanceof SSLHandshakeException) {
            rThrowable = new ResponseThrowable(throwable, Error.SSL_ERROR);
            rThrowable.errorMsg = "证书验证失败";
            return rThrowable;
        } else if (throwable instanceof ConnectTimeoutException) {
            rThrowable = new ResponseThrowable(throwable, Error.TIMEOUT_ERROR);
            rThrowable.errorMsg = "连接超时";
            return rThrowable;
        } else {
            rThrowable = new ResponseThrowable(throwable, Error.UNKNOWN);
            rThrowable.errorMsg = "未知错误";
            return rThrowable;
        }
    }
    public static class ResponseThrowable extends Exception {
        public int errorCode;
        public String errorMsg;

        public ResponseThrowable(Throwable throwable, int errorCode) {
            super(throwable);
            this.errorCode = errorCode;
        }
        public int getErrorCode(){return errorCode;}
        public String getErrorMsg() {
            return errorMsg;
        }
    }
    public static class Error {
        public static final int UNKNOWN = 1000;
        public static final int PARSE_ERROR = 1001;
        public static final int NETWORK_ERROR = 1002;
        public static final int HTTP_ERROR = 1003;
        public static final int SSL_ERROR = 1004;
        public static final int TIMEOUT_ERROR = 1005;
    }
    public static class ServerException extends RuntimeException {
        public int errorCode;
        public String errorMsg;
    }
}
