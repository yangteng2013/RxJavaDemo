package com.noe.rxjava.parallaxviewpager;

import android.support.v7.widget.RecyclerView;

/**
 * Created by lijie24 on 2017/5/4.
 */

public abstract class RecyclerViewFragment extends ScrollTabHolderFragment {

    protected RecyclerView mRecyclerView;
    protected int mScrollY;

    protected abstract void setScrollOnLayoutManager(int scrollY);

    protected void setRecyclerViewOnScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mScrollY += dy;

                if (mScrollTabHolder != null) {
                    mScrollTabHolder.onRecyclerViewScroll(recyclerView, dx, dy, mScrollY, mPosition);
                }
            }
        });
    }

    @Override
    public void adjustScroll(int scrollHeight, int headerHeight) {
        if (mRecyclerView == null) {
            return;
        }

        mScrollY = headerHeight - scrollHeight;
        setScrollOnLayoutManager(mScrollY);
    }
}