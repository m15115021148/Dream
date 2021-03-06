package com.romantic.dream.http.okhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求添加头部
 * Created by chenMeng on 2017/9/14.
 */

public class HeadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
//                .addHeader("client_uuid", MyApplication.clientsModel==null?"":MyApplication.clientsModel.getClient_uuid())
//                .addHeader("api_password", MyApplication.clientsModel==null?"":MyApplication.clientsModel.getApi_password())
                .build();
        return chain.proceed(request);
    }
}
