package com.koekoetech.clockify.helpers;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.interfaces.NetAdapterConfig;

/**
 * Created by Wai Phyo Aung on 2019-10-07.
 */
public class NetAdapterConfigImpl implements NetAdapterConfig {

    @LayoutRes
    private int emptyLayout = R.layout.layout_rv_empty_view;

    @Override
    public int getInitLoadingLayout() {
        return R.layout.layout_rv_initial_loading;
    }

    @Override
    public int getInitFailureLayout() {
        return R.layout.layout_rv_initial_error;
    }

    @Override
    public int getEmptyViewLayout() {
        return emptyLayout;
    }

    @Nullable
    @Override
    public LoadMoreView getLoadMoreView() {
        return new CustomLoadMoreView();
    }

    public void setEmptyLayout(@LayoutRes int emptyLayout) {
        this.emptyLayout = emptyLayout;
    }
}
