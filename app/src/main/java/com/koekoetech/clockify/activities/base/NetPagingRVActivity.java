package com.koekoetech.clockify.activities.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.interfaces.NetAdapterConfig;
import com.koekoetech.clockify.rest.RetrofitCallbackHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by Zaw Myo Naing on 7/11/18.
 * <p>
 * <pre>
 *     Description  :   An abstract template activity for displaying
 *                      paged list from network using Retrofit and
 *                      base adapter libraries.
 *
 *    Generic Types :   M (Type of Retrofit response type)
 *                      E (Type for showing in BRVAH adapter)
 *                      D (Type of BRAVH adapter)
 *
 *    Dependencies  :   Depended Interfaces And Classes
 *
 *                      Classes
 *                      -------
 *
 *                      1. {@link BaseActivity}
 *                      2. {@link RetrofitCallbackHelper}
 *
 *                      Interfaces
 *                      ----------
 *
 *                      1. {@link NetAdapterConfig}
 *
 *
 *                      @see <a href="http://square.github.io/retrofit/">Retrofit</a>
 *                      @see <a href="http://www.recyclerview.org/">BRVAH (Recyler View Adapter Library)</a>
 * </pre>
 **/
public abstract class NetPagingRVActivity<M, E, D extends BaseQuickAdapter<E, ? extends BaseViewHolder>> extends BaseActivity {

    private static final String TAG = "NetPagingRVActivity";

    private int currentPage;

    private Call<M> retrofitCallback;

    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(TAG, "NetPagingRVActivity:onCreate: called");

        setupContents(savedInstanceState);

        getSwipeRefresh().setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        getSwipeRefresh().setOnRefreshListener(() -> {
            if (isLoading) {
                getSwipeRefresh().setRefreshing(false);
                return;
            }
            onSwipeRefreshed();
            onRefresh();
        });

        getRecyclerView().setAdapter(getRecyclerAdapter());

        if (getAdapterConfig().getLoadMoreView() != null) {
            // Set custom view for loading, error and no more data
            getRecyclerAdapter().setLoadMoreView(getAdapterConfig().getLoadMoreView());
        }

        // Configuring Endless Loading
        getRecyclerAdapter().setOnLoadMoreListener(this::loadMore, getRecyclerView());

        if (isCacheExist()) {
            getRecyclerAdapter().loadMoreEnd();
            return;
        }
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        if (retrofitCallback != null) {
            retrofitCallback.cancel();
        }
        super.onDestroy();
    }

    protected void onRefresh() {
        Log.d(TAG, "onRefresh() called");
        currentPage = 1;
        getSwipeRefresh().setRefreshing(false);
        getRecyclerAdapter().setEnableLoadMore(false);

        getRecyclerAdapter().setNewData(new ArrayList<>());

        setEmptyView(getAdapterConfig().getInitLoadingLayout());

        loadData(new RetrofitCallbackHelper<M>() {
            @Override
            protected void onSuccess(M data, int responseCode) {
                isLoading = false;
                getSwipeRefresh().setRefreshing(false);
                getRecyclerAdapter().setEnableLoadMore(true);

                setEmptyView(getAdapterConfig().getEmptyViewLayout());
                showData(true, data);
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                isLoading = false;
                getSwipeRefresh().setRefreshing(false);
                getRecyclerAdapter().setEnableLoadMore(true);

                setEmptyView(getAdapterConfig().getInitFailureLayout());
            }
        });

    }

    protected void loadMore() {
        getRecyclerView().postDelayed(() -> loadData(new RetrofitCallbackHelper<M>() {
            @Override
            protected void onSuccess(M data, int responseCode) {
                isLoading = false;
                showData(false, data);
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                isLoading = false;
                getRecyclerAdapter().loadMoreFail();
            }
        }), 200);
    }

    private void loadData(RetrofitCallbackHelper<M> dataLoadCallback) {
        isLoading = true;
        retrofitCallback = getRetrofitCall(currentPage);
        retrofitCallback.enqueue(dataLoadCallback);
    }

    private void showData(boolean isRefresh, M dataList) {

        // Increase current page for next load
        currentPage++;

        List<E> entityList = onDataReceived(dataList);

        // Getting Data List
        int entityListSize = entityList == null ? 0 : entityList.size();

        if (isRefresh) {
            // If refreshing clear all existing item and display newly fetched list
            getRecyclerAdapter().setNewData(onDataReceived(dataList));
        } else {
            if (entityListSize > 0) {
                getRecyclerAdapter().addData(onDataReceived(dataList));
            }
        }

        if (entityListSize > 0) {
            // if newly fetched data is not empty, then continue to loaded next page
            getRecyclerAdapter().loadMoreComplete();
        } else {
            // No more data
            getRecyclerAdapter().loadMoreEnd(isRefresh);
        }

    }

    private void setEmptyView(@LayoutRes int emptyViewLayout) {
        if (emptyViewLayout != 0) {
            getRecyclerAdapter().setEmptyView(View.inflate(this, emptyViewLayout, null));
        }
    }

    protected abstract void setupContents(Bundle savedInstanceState);

    protected abstract boolean isCacheExist();

    /**
     * This method allows to process the list received from
     * retrofit response before showing in adapter.
     *
     * @param data List of data received from retrofit response
     * @return List of data to showing adapter
     */
    protected abstract List<E> onDataReceived(M data);

    @NonNull
    protected abstract RecyclerView getRecyclerView();

    @NonNull
    protected abstract D getRecyclerAdapter();

    @NonNull
    protected abstract SwipeRefreshLayout getSwipeRefresh();

    @NonNull
    protected abstract Call<M> getRetrofitCall(int currentPage);

    @NonNull
    protected abstract NetAdapterConfig getAdapterConfig();

    protected abstract void onSwipeRefreshed();

}