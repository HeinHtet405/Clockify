package com.koekoetech.clockify.dialogs.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.koekoetech.clockify.R;


/**
 * Created by Wai Phyo Aung on 2019-10-12.
 */
public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomAlertDialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewReady(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        @Nullable final Dialog dialog = getDialog();
        if (dialog != null) {
            @Nullable final Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public void show(@Nullable FragmentManager fragmentManager) {
        try {
            if (fragmentManager != null) {
                super.show(fragmentManager, getFragmentTag());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    @NonNull
    public abstract String getFragmentTag();

    protected abstract void onViewReady(@NonNull View view, @Nullable Bundle savedInstanceState);
}
