package com.randall.musicclient.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.randall.musicclient.client.MusicClient;
import com.randall.musicclient.entity.Proxy;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 10:56
 */
public class HttpProxyClientFactory {
    
    public static MusicClient musicClient(Proxy proxy, int timeoutInSecond) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeoutInSecond, TimeUnit.SECONDS)
                .readTimeout(timeoutInSecond, TimeUnit.SECONDS)
                .addInterceptor(new HttpHeaderInterceptor());
        addProxy(proxy, builder);
        OkHttpClient okHttpClient = builder.build();
        ObjectMapper mapper = new ObjectMapper();
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .baseUrl("http://music.163.com/")
                .client(okHttpClient)
                .build();
        return retrofit.create(MusicClient.class);
    }

    /**
     * 设置代理
     */
    private static void addProxy(Proxy proxy, OkHttpClient.Builder builder) {
        if (proxy != null) {
            String schemeName = proxy.getProto();
            java.net.Proxy.Type type = java.net.Proxy.Type.HTTP;
            if (schemeName.startsWith("socks")) {
                type = java.net.Proxy.Type.SOCKS;
            }
            builder.proxy(new java.net.Proxy(type, new InetSocketAddress(proxy.getIp(), proxy.getPort())));
        }
    }
}
