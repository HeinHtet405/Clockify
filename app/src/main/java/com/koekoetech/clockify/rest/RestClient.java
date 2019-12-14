package com.koekoetech.clockify.rest;

import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koekoetech.clockify.BuildConfig;
import com.koekoetech.clockify.rest.endpoints.ProjectEndpoints;
import com.koekoetech.clockify.rest.endpoints.TimeEntryEndpoints;
import com.koekoetech.clockify.rest.endpoints.UserEndpoints;

import java.util.Collections;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit sRetrofit;

    private static Retrofit getsRetrofit(String apiKey) {
        if (sRetrofit == null) {

            OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
            okClientBuilder.addInterceptor(new HeaderInterceptor(apiKey));

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                okClientBuilder.addInterceptor(logging);
            }

            if (Build.VERSION.SDK_INT < 20) {
                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                        .build();
                okClientBuilder.connectionSpecs(Collections.singletonList(spec));
            }

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setExclusionStrategies()
                    .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                    .create();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.clockify.me/api/v1/")
                    .client(okClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

        return sRetrofit;
    }

    public static void cancel(@NonNull Call... calls) {
        for (Call call : calls) {
            try {
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ProjectEndpoints getProjectEndpoints(String apiKey) {
        return getsRetrofit(apiKey).create(ProjectEndpoints.class);
    }

    public static UserEndpoints getUserEndpoints(String apiKey) {
        return getsRetrofit(apiKey).create(UserEndpoints.class);
    }

    public static TimeEntryEndpoints getTimeEntryEndpoints(String apiKey) {
        return getsRetrofit(apiKey).create(TimeEntryEndpoints.class);
    }

}
