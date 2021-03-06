package com.jpfeng.framework.data.net;

import com.jpfeng.framework.data.net.interceptor.LogInterceptor;
import com.jpfeng.framework.data.net.util.NetConfig;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Author: Jpfeng
 * E-mail: fengjup@live.com
 * Date: 2018/5/4
 */
class HttpClient {

    /**
     * 全局的 HttpClient, 用于 http 请求。
     */
    private static volatile OkHttpClient mInstance;

    static OkHttpClient getInstance() {
        if (null == mInstance) {
            synchronized (HttpClient.class) {
                if (null == mInstance) {
                    mInstance = createInstance();
                }
            }
        }
        return mInstance;
    }

    private static OkHttpClient createInstance() {
        NetConfig config = NetClient.getInstance().getConfig();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.connectTimeout(), config.timeUnit())
                .readTimeout(config.readTimeout(), config.timeUnit())
                .writeTimeout(config.writeTimeout(), config.timeUnit());
        List<Interceptor> interceptors = config.httpInterceptors();
        for (Interceptor i : interceptors) {
            builder.addInterceptor(i);
        }
        // 要保证 LogInterceptor 是最后一个被添加，才能打印出完整 log
        builder.addInterceptor(new LogInterceptor());
        return builder.build();
    }

    static synchronized void reconstruct() {
        mInstance = createInstance();
    }
}
