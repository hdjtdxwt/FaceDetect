package com.epsit.ihealth.robot.retrofit;

import android.text.TextUtils;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 网络请求的拦截器类，主要是在调试代码的时候可以输出拦截到的请求的参数等信息
 * Created by Nicholas on 2016/11/6.
 */

public class NetWorkInterceptor implements Interceptor {
    static String TAG ="NetWorkInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpLoggingInterceptor.Level level = this.level;

        Request request = chain.request();

        //如果Log Level 级别为NONOE，则不打印，直接返回
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        //是否打印body
        boolean logBody = (level == Level.BODY);
        //是否打印header
        boolean logHeaders = logBody || level == Level.HEADERS;

        //获得请求body
        RequestBody requestBody = request.body();
        //请求body是否为空
        boolean hasRequestBody = requestBody != null;

        //获得Connection，内部有route、socket、handshake、protocol方法
        Connection connection = chain.connection();
        //如果Connection为null，返回HTTP_1_1，否则返回connection.protocol()
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        //比如: --> POST http://121.40.227.8:8088/api http/1.1
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        Log.d(TAG,requestStartMessage);

        //打印 Request
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    Log.d(TAG,"Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    Log.d(TAG,"Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Log.d(TAG,name + ": " + headers.value(i));
                }
            }
            if (!logBody || !hasRequestBody) {
                Log.d(TAG,"--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Log.d(TAG,"--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                //编码设为UTF-8
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                Log.d(TAG,"");
                if (isPlaintext(buffer)) {
                    Log.d(TAG,buffer.readString(charset));
                    Log.d(TAG,"--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    Log.d(TAG,"--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }
        /*String token = RobotLocalOperator.getInstance().getAccessToken();
        String userId = RobotLocalOperator.getInstance().getRobotId();
        if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)){
            Request.Builder builder = request.newBuilder();
            builder.addHeader("token", token);
            builder.addHeader("userId",userId);
            request = builder.build(); //如果token和userId有的话，会重新创建一个token并添加到头部
        }*/

        //打印 Response
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Log.d(TAG,"<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        //比如 <-- 200 OK http://121.40.227.8:8088/api (36ms)
        Log.d(TAG,"<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                Log.d(TAG,headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpEngine.hasBody(response)) {
                Log.d(TAG,"<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                Log.d(TAG,"<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        Log.d(TAG,"");
                        Log.d(TAG,"Couldn't decode the response body; charset is likely malformed.");
                        Log.d(TAG,"<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    Log.d(TAG,"");
                    Log.d(TAG,"<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    Log.d(TAG,"");

                    //获取Response的body的字符串 并打印
                    Log.d(TAG,buffer.clone().readString(charset));
                }

                Log.d(TAG,"<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}