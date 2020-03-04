package com.koekoetech.clockify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.button.MaterialButton;
import com.koekoetech.clockify.BuildConfig;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.dbStorage.ProjectDbAccess;
import com.koekoetech.clockify.helpers.AppProgressDialogHelper;
import com.koekoetech.clockify.helpers.SharePreferenceHelper;
import com.koekoetech.clockify.models.Project;
import com.koekoetech.clockify.models.UserInfo;
import com.koekoetech.clockify.rest.RestClient;
import com.koekoetech.clockify.rest.RetrofitCallbackHelper;

import java.util.List;
import java.util.Objects;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import retrofit2.Call;

public class ApiKeyActivity extends AppCompatActivity {

    @BindView(R.id.activity_api_key_tv_version)
    AppCompatTextView tvVersion;

    @BindView(R.id.activity_api_key_btn_login)
    MaterialButton btnLogin;

    @BindView(R.id.activity_api_key_et_key)
    AppCompatEditText etApiKey;

    @BindString(R.string.lbl_version)
    String version;

    @BindColor(R.color.colorHintText)
    int colorHint;

    private UserInfo userInfo;
    private Realm mRealm;
    private Unbinder unbinder;
    private ProjectDbAccess projectDbAccess;
    private SharePreferenceHelper sharePreferenceHelper;
    private AppProgressDialogHelper appProgressDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_key);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        projectDbAccess = new ProjectDbAccess(mRealm);
        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        appProgressDialogHelper = new AppProgressDialogHelper(this);
        appProgressDialogHelper.setMessage("Please wait...");
        String versionName = version + BuildConfig.VERSION_NAME;
        tvVersion.setText(versionName);
        userInfo = sharePreferenceHelper.getUserInformation();
    }

    @OnClick(R.id.activity_api_key_btn_login)
    public void onClickLogin() {
        String apiKey = Objects.requireNonNull(etApiKey.getText()).toString();
        if (!TextUtils.isEmpty(apiKey)) {
            if (apiKey.length() == 16) {
                sharePreferenceHelper.setApiKey(apiKey);
                appProgressDialogHelper.show();
                if (userInfo == null) {
                    getUserInfo();
                } else {
                    goToMainActivity();
                }
            } else {
                Toast.makeText(this, "Please enter your valid api key", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter your api key", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserInfo() {
        Call<UserInfo> userInfoCall = RestClient.getUserEndpoints(sharePreferenceHelper.getApiKey()).getUserInfo();
        userInfoCall.enqueue(new RetrofitCallbackHelper<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo data, int responseCode) {
                sharePreferenceHelper.setUserInformation(data);
                getProjectData();
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                appProgressDialogHelper.dismiss();
                Toast.makeText(ApiKeyActivity.this, "Login failed.Please try again...", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void getProjectData() {
        userInfo = sharePreferenceHelper.getUserInformation();
        Call<List<Project>> projectCall = RestClient.getProjectEndpoints(sharePreferenceHelper.getApiKey()).getProjectList(userInfo.getActiveWorkspace(), 100);
        projectCall.enqueue(new RetrofitCallbackHelper<List<Project>>() {
            @Override
            protected void onSuccess(List<Project> data, int responseCode) {
                appProgressDialogHelper.dismiss();
                for (Project project : data) {
                    projectDbAccess.insertUpdateProject(project);
                }
                sharePreferenceHelper.setLogIn(true);
                goToMainActivity();
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                appProgressDialogHelper.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }
}
