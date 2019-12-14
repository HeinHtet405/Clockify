package com.koekoetech.clockify.activities.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.koekoetech.clockify.R;
import com.koekoetech.clockify.interfaces.OnBackPressedListener;
import com.koekoetech.clockify.interfaces.OnHomePressedListener;


/**
 * Created by Hein Htet Aung on 2019-11-18.
 **/
@SuppressWarnings("unused")
public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatTextView tvToolbarTitle;
    private boolean isActive;

    private OnBackPressedListener onBackPressedListener;
    private OnHomePressedListener onHomePressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvToolbarTitle = findViewById(R.id.toolbar_tvTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (onHomePressedListener != null) {
                onHomePressedListener.doHome();
            } else {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * <pre>
     *     To be implemented by child class ,
     *     in order to provide layout xml ID for current activity
     * </pre>
     *
     * @return Layout Resource ID for current activity
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    /**
     * @return Toolbar instance of current activity or null if toolbar not provided
     */
    protected Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * <pre>
     *     Setup toolbar
     * </pre>
     *
     * @param isChild Flag to indicate if this activity is called by another activity
     */
    protected void setupToolbar(boolean isChild) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(isChild);
            }
        }
    }

    /**
     * Sets toolbar title
     *
     * @param text Toolbar Title
     */
    protected void setupToolbarText(String text) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(text);
        }
    }

    protected void setupToolbarText(String text, int gravityType) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(text);
            tvToolbarTitle.setGravity(gravityType);
        }
    }

    /**
     * Sets toolbar title
     *
     * @param stringId Toolbar title string ID
     */
    protected void setupToolbarText(@StringRes int stringId) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(getResources().getString(stringId));
        }
    }

    /**
     * Update Toolbar color at runtime
     *
     * @param color Toolbar color
     */
    protected void setupToolbarBgColor(String color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(color));
        }
    }

    /**
     * Update Toolbar color at runtime
     *
     * @param color Toolbar color resource ID
     */
    protected void setupToolbarBgColor(@ColorInt int color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
    }

    /**
     * Update Toolbar text color at runtime
     *
     * @param color Toolbar text color
     */
    protected void setupToolbarTextColor(String color) {
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.parseColor(color));
        }
    }

    /**
     * Update Toolbar text color at runtime
     *
     * @param color Toolbar text color
     */
    protected void setupToolbarTextColor(@ColorInt int color) {
        if (toolbar != null) {
            toolbar.setTitleTextColor(color);
        }
    }

    /**
     * Change Primary Dark color and Primary Color for current activity at runtime
     *
     * @param colorPrimaryDark Primary Dark Color Resource ID
     * @param colorPrimary     Primary Color Resource ID
     */
    protected void setUpTheme(@ColorRes int colorPrimaryDark, @ColorRes int colorPrimary) {
        int colorDark = ContextCompat.getColor(this, colorPrimaryDark);

        if (toolbar != null) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, colorPrimary));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(colorDark);
            getWindow().setStatusBarColor(colorDark);
        }
    }

    protected void changeUpIndicator(@DrawableRes int icon) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(icon);
        }
    }

    protected boolean isActive() {
        return isActive;
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public void setOnHomePressedListener(OnHomePressedListener onHomePressedListener) {
        this.onHomePressedListener = onHomePressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        } else {
            super.onBackPressed();
        }
    }

}
