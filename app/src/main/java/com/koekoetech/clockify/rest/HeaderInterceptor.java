package com.koekoetech.clockify.rest;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private String apiKey;

    HeaderInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Api-Key", apiKey);
        request = builder.build();
        return chain.proceed(request);
    }
}
