package com.koekoetech.clockify.dialogs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.dialogs.base.BaseDialogFragment;
import com.koekoetech.clockify.models.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileDialog extends BaseDialogFragment {

    public static final String TAG = "ProfileDialog";

    @BindView(R.id.dialog_profile_iv)
    AppCompatImageView ivProfile;

    @BindView(R.id.dialog_profile_tv_name)
    AppCompatTextView tvName;

    @BindView(R.id.dialog_profile_tv_email)
    AppCompatTextView tvEmail;

    private Unbinder unbinder;
    private UserInfo userInfo;

    public ProfileDialog(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_profile;
    }

    @NonNull
    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected void onViewReady(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setCancelable(true);
        unbinder = ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        Glide.with(this)
                .load(userInfo.getProfilePicture())
                .placeholder(R.drawable.img_logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);
        tvName.setText(userInfo.getName());
        tvEmail.setText(userInfo.getEmail());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
