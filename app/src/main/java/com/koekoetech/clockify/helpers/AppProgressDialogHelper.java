package com.koekoetech.clockify.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.koekoetech.clockify.R;

/**
 * Created by ZMN on 6/22/18.
 **/

@SuppressWarnings({"unused"})
public class AppProgressDialogHelper extends AlertDialog {

    private static final String TAG = "AppProgressDialog";

    private CharSequence message;

    @ColorRes
    private int progressColor = R.color.colorAccent;

    public AppProgressDialogHelper(@NonNull Context context) {
        super(context);
    }

    public AppProgressDialogHelper(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AppProgressDialogHelper(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @Nullable final Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
        }

        setContentView(R.layout.dialog_app_progress);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        initMessage();
    }

    @Override
    public void setMessage(CharSequence message) {
        Log.d(TAG, "setMessage() called with: message = [" + message + "]");
        this.message = message;
        if (isShowing()) {
            initMessage();
        }
    }

    @Override
    public void show() {
        try {
            if (!isShowing()) {
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMessage() {
        Log.d(TAG, "initMessage() called");
        TextView dialogTitleTv = findViewById(R.id.progress_tvTitle);
        if (dialogTitleTv != null) {
            Log.d(TAG, "initMessage: Setting Dialog Title");
            if (!TextUtils.isEmpty(message)) {
                dialogTitleTv.setText(message);
                dialogTitleTv.setVisibility(View.VISIBLE);
            } else {
                dialogTitleTv.setVisibility(View.GONE);
            }
        }
    }
}
