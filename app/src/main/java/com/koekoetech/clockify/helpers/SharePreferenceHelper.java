package com.koekoetech.clockify.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.models.UserInfo;

public class SharePreferenceHelper {

    private static final String PREF_USER_INFO = "user_information";
    private static final String PREF_IS_LOG_IN = "log_in";
    private static final String PREF_API_KEY = "api_key";
    private static final String PREF_MONTH_CHECK = "month_check";

    private ContextHelper contextHelper;

    public static SharePreferenceHelper getHelper(Context context) {
        SingletonHelper.accessor.init(context);
        return SingletonHelper.accessor;
    }

    private void init(Context context) {
        contextHelper = new ContextHelper(context);
    }

    private SharedPreferences getSharedPreferences() {
        return contextHelper.getContext().getSharedPreferences(contextHelper.getContext().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        return getSharedPreferences().edit();
    }

    private static class SingletonHelper {
        private static final SharePreferenceHelper accessor = new SharePreferenceHelper();
    }

    private class ContextHelper {
        private Context context;

        ContextHelper(Context context) {
            this.context = context;
        }

        Context getContext() {
            return context;
        }
    }

    /* LOGIN [START] */
    public boolean isLogIn() {
        return getSharedPreferences().getBoolean(PREF_IS_LOG_IN, false);
    }

    public void setLogIn(boolean flag) {
        getSharedPreferencesEditor().putBoolean(PREF_IS_LOG_IN, flag).apply();
    }
    /* LOGIN [END] */

    /* API KEY [START] */
    public String getApiKey() {
        return getSharedPreferences().getString(PREF_API_KEY, "");
    }

    public void setApiKey(String apiKey) {
        getSharedPreferencesEditor().putString(PREF_API_KEY, apiKey).apply();
    }
    /* API KEY [END] */

    /* USER INFO [START] */
    public UserInfo getUserInformation() {
        String userInfo = getSharedPreferences().getString(PREF_USER_INFO, "");
        if (!TextUtils.isEmpty(userInfo)) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(userInfo, UserInfo.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setUserInformation(UserInfo user) {
        String userInfo = "";
        if (user != null) {
            Gson gson = new Gson();
            userInfo = gson.toJson(user);
        }
        getSharedPreferencesEditor().putString(PREF_USER_INFO, userInfo).apply();
    }
    /* USER INFO [END] */

    public boolean getDateType() {
        return getSharedPreferences().getBoolean(PREF_MONTH_CHECK, false);
    }

    public void setDateType(boolean isMonthCheck) {
        getSharedPreferencesEditor().putBoolean(PREF_MONTH_CHECK, isMonthCheck).apply();
    }

    /* CLEAR SHARE PREFERENCE [START] */
    public void clearSharedPreferences() {
        getSharedPreferencesEditor().clear().apply();
    }
    /* CLEAR SHARE PREFERENCE [END] */

}
