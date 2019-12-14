package com.koekoetech.clockify.fragments.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
 * Created by Zaw Myo Naing on 7/31/18.
 * <p>
 * <pre>
 *     Description  :   An abstract template fragment for displaying
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
 *                      1. {@link RetrofitCallbackHelper}
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
@SuppressWarnings("WeakerAccess")
public abstract class NetPagingRVFragment<M, E, D extends BaseQuickAdapter<E, ? extends BaseViewHolder>> extends Fragment {

    private static final String TAG = "NetPagingRVFragment";

    private int currentPage;

    private Call<M> retrofitCallback;

    private boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "NetPagingRVFragment:onViewCreated: called");

        onViewReady(view, savedInstanceState);

        @Nullable SwipeRefreshLayout swipeRefresh = getSwipeRefresh();
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
            swipeRefresh.setOnRefreshListener(() -> {
                if (isLoading) {
                    endRefreshing();
                    return;
                }
                onSwipeRefreshed();
                onRefresh();
            });
        }

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
    public void onDestroyView() {
        if (retrofitCallback != null) {
            retrofitCallback.cancel();
        }
        super.onDestroyView();
    }

    protected void onRefresh() {
        Log.d(TAG, "onRefresh() called");
        currentPage = 1;
        getRecyclerAdapter().setEnableLoadMore(false);
        getRecyclerAdapter().setNewData(new ArrayList<>());
        endRefreshing();

        setEmptyView(getAdapterConfig().getInitLoadingLayout());

        loadData(new RetrofitCallbackHelper<M>() {
            @Override
            protected void onSuccess(M data, int responseCode) {
                isLoading = false;
                getRecyclerAdapter().setEnableLoadMore(true);

                setEmptyView(getAdapterConfig().getEmptyViewLayout());
                showData(true, data);
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                isLoading = false;
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
        @Nullable final FragmentActivity activity = getActivity();
        if (activity != null && emptyViewLayout != 0) {
            getRecyclerAdapter().setEmptyView(View.inflate(activity, emptyViewLayout, null));
        }
    }

    /**
     * <pre>
     *     To be implemented by child class ,
     *     in order to provide layout xml ID for current fragment
     * </pre>
     *
     * @return Layout Resource ID for current activity
     */
    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void onViewReady(View view, @Nullable Bundle savedInstanceState);

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

    @Nullable
    protected abstract SwipeRefreshLayout getSwipeRefresh();

    @NonNull
    protected abstract Call<M> getRetrofitCall(int currentPage);

    @NonNull
    protected abstract NetAdapterConfig getAdapterConfig();

    protected abstract void onSwipeRefreshed();

    private void endRefreshing() {
        @Nullable SwipeRefreshLayout swipeRefresh = getSwipeRefresh();
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
    }

}
