package com.randall.musicclient.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 11:19
 */
public class HttpHeaderInterceptor implements Interceptor {
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("User-Agent", UAPool.getUA());
        builder.addHeader("Connection", "keep-alive/");
        return chain.proceed(builder.build());
    }
}
