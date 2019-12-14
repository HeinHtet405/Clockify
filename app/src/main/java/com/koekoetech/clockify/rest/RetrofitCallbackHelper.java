package com.koekoetech.clockify.rest;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zaw Myo Naing on 3/18/18.
 **/
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class RetrofitCallbackHelper<T> implements Callback<T> {

    /**
     * Flag to indicate http response code is not within 200...300
     */
    public static final int RESULT_NET_FAIL = 9384;

    /**
     * Flag to indicate http request failure (eg. Invoked network request when internet connection is not available)
     */
    public static final int RESULT_CLIENT_FAIL = 3948;

    /**
     * Flag to indicate http response body is null
     */
    public static final int RESULT_NO_DATA = 2932;

    /**
     * Flag to indicate when no http response code is received
     */
    public static final int NO_RESPONSE_CODE = -3246;

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        final int responseCode = response.code();
        if (response.isSuccessful()) {
            T data = response.body();
            if (data != null) {
                onSuccess(data, responseCode);
            } else {
                onFailure(new Throwable("Received Invalid data from response"), responseCode, RESULT_NO_DATA);
            }
        } else {
            String errMsg = "Unsuccessful Response\nResponse Code : " + response.code() + "\nMessage: " + response.message();
            onFailure(new Throwable(errMsg), responseCode, RESULT_NET_FAIL);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        String errMsg = !TextUtils.isEmpty(t.getMessage()) ? t.getMessage() : "Unknown Error";
        onFailure(new Throwable(errMsg), NO_RESPONSE_CODE, RESULT_CLIENT_FAIL);
    }

    protected abstract void onSuccess(T data, int responseCode);

    protected abstract void onFailure(Throwable t, int responseCode, int resultCode);

}
