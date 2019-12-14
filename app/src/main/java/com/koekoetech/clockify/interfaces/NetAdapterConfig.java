package com.koekoetech.clockify.interfaces;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.koekoetech.clockify.activities.base.NetPagingRVActivity;
import com.koekoetech.clockify.fragments.base.NetPagingRVFragment;

/**
 * Created by Zaw Myo Naing on 7/16/18.
 * <pre>
 *     Description  :   Required Config Provider Interface for
 *                      {@link NetPagingRVActivity}
 *                      {@link NetPagingRVFragment}
 * </pre>
 **/
public interface NetAdapterConfig {

    @LayoutRes
    int getInitLoadingLayout();

    @LayoutRes
    int getInitFailureLayout();

    @LayoutRes
    int getEmptyViewLayout();

    @Nullable
    LoadMoreView getLoadMoreView();

}