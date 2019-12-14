package com.koekoetech.clockify.helpers;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.koekoetech.clockify.R;

/**
 * Created by Wai Phyo Aung on 2019-10-07.
 */
public class CustomLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.layout_load_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loading_progress;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.retry_container;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_end;
    }
}
