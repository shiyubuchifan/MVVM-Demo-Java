package cn.snow.network;

import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

public class ExceptionHandle {

    public static final int HTTP_ERROR_CODE_400 = 400;//	Bad Request	客户端请求的语法错误，服务器无法理解
    public static final int HTTP_ERROR_CODE_401 = 401;//	Unauthorized	请求要求用户的身份认证
    public static final int HTTP_ERROR_CODE_402 = 402;//	Payment Required	保留，将来使用
    public static final int HTTP_ERROR_CODE_403 = 403;//	Forbidden	服务器理解请求客户端的请求，但是拒绝执行此请求
    public static final int HTTP_ERROR_CODE_404 = 404;//	Not Found	服务器无法根据客户端的请求找到资源（网页）。通过此代码，网站设计人员可设置"您所请求的资源无法找到"的个性页面
    public static final int HTTP_ERROR_CODE_405 = 405;//	Method Not Allowed	客户端请求中的方法被禁止
    public static final int HTTP_ERROR_CODE_406 = 406;//	Not Acceptable	服务器无法根据客户端请求的内容特性完成请求
    public static final int HTTP_ERROR_CODE_407 = 407;//	Proxy Authentication Required	请求要求代理的身份认证，与401类似，但请求者应当使用代理进行授权
    public static final int HTTP_ERROR_CODE_408 = 408;//	Request Time-out	服务器等待客户端发送的请求时间过长，超时
    public static final int HTTP_ERROR_CODE_409 = 409;//	Conflict	服务器完成客户端的 PUT 请求时可能返回此代码，服务器处理请求时发生了冲突
    public static final int HTTP_ERROR_CODE_410 = 410;//	Gone	客户端请求的资源已经不存在。410不同于404，如果资源以前有现在被永久删除了可使用410代码，网站设计人员可通过301代码指定资源的新位置
    public static final int HTTP_ERROR_CODE_411 = 411;//	Length Required	服务器无法处理客户端发送的不带Content-Length的请求信息
    public static final int HTTP_ERROR_CODE_412 = 412;//	Precondition Failed	客户端请求信息的先决条件错误
    public static final int HTTP_ERROR_CODE_413 = 413;//	Request Entity Too Large	由于请求的实体过大，服务器无法处理，因此拒绝请求。为防止客户端的连续请求，服务器可能会关闭连接。如果只是服务器暂时无法处理，则会包含一个Retry-After的响应信息
    public static final int HTTP_ERROR_CODE_414 = 414;//	Request-URI Too Large	请求的URI过长（URI通常为网址），服务器无法处理
    public static final int HTTP_ERROR_CODE_415 = 415;//	Unsupported Media Type	服务器无法处理请求附带的媒体格式
    public static final int HTTP_ERROR_CODE_416 = 416;//	Requested range not satisfiable	客户端请求的范围无效
    public static final int HTTP_ERROR_CODE_417 = 417;//	Expectation Failed	服务器无法满足Expect的请求头信息
    public static final int HTTP_ERROR_CODE_500 = 500;//	Internal Server Error	服务器内部错误，无法完成请求
    public static final int HTTP_ERROR_CODE_501 = 501;//	Not Implemented	服务器不支持请求的功能，无法完成请求
    public static final int HTTP_ERROR_CODE_502 = 502;//	Bad Gateway	作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应
    public static final int HTTP_ERROR_CODE_503 = 503;//	Service Unavailable	由于超载或系统维护，服务器暂时的无法处理客户端的请求。延时的长度可包含在服务器的Retry-After头信息中
    public static final int HTTP_ERROR_CODE_504 = 504;//	Gateway Time-out	充当网关或代理的服务器，未及时从远端服务器获取请求
    public static final int HTTP_ERROR_CODE_505 = 505;//	HTTP Version not supported	服务器不支持请求的HTTP协议的版本，无法完成处理
    public static final int ERROR_GSON_PARSE = -1;//	json相关错误
    public static final int ERROR_NET_CONNECT = -2;//	net连接失败
    public static final int ERROR_SSL = -3;//	SSL证书验证失败
    public static final int ERROR_TIMEOUT = -4;//	连接超时
    public static final int ERROR_DNS_PARSE = -5;//	DNS解析失败
    public static final int ERROR_UNKNOW = -999;//	未知错误


    public static ResponseThrowable handleServerException(Throwable throwable) {
        ResponseThrowable responseThrowable;
        if (throwable instanceof HttpException) {
            responseThrowable = new ResponseThrowable(throwable, ((HttpException) throwable).code());
            switch (((HttpException) throwable).code()) {
                case HTTP_ERROR_CODE_400:
                default:
                    responseThrowable.message = "网络错误";
                    break;

            }
            return responseThrowable;
        } else if (throwable instanceof ServerException) {
            ServerException serverException = (ServerException) throwable;
            responseThrowable = new ResponseThrowable(serverException, serverException.code);
            responseThrowable.message = serverException.message;
            return responseThrowable;
        } else if (throwable instanceof JsonParseException || throwable instanceof JSONException || throwable instanceof ParseException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_GSON_PARSE);
            responseThrowable.message = "数据解析错误";
            return responseThrowable;
        } else if (throwable instanceof ConnectException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_NET_CONNECT);
            responseThrowable.message = "网络连接错误";
            return responseThrowable;
        } else if (throwable instanceof SSLHandshakeException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_SSL);
            responseThrowable.message = "SSL证书验证失败";
            return responseThrowable;
        } else if (throwable instanceof ConnectTimeoutException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_TIMEOUT);
            responseThrowable.message = "连接超时";
            return responseThrowable;
        } else if (throwable instanceof SocketTimeoutException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_TIMEOUT);
            responseThrowable.message = "连接超时";
            return responseThrowable;
        } else if (throwable instanceof UnknownHostException) {
            responseThrowable = new ResponseThrowable(throwable, ERROR_DNS_PARSE);
            responseThrowable.message = "DNS解析失败";
            return responseThrowable;
        } else {
            responseThrowable = new ResponseThrowable(throwable, ERROR_UNKNOW);
            responseThrowable.message = "未知错误";
            return responseThrowable;
        }
    }


    public static class ResponseThrowable extends Exception {
        public int code;
        public String message;

        public ResponseThrowable(Throwable cause, int code) {
            super(cause);
            this.code = code;
        }

        @Nullable
        @Override
        public String getMessage() {
            return message;
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String message;
    }
}
