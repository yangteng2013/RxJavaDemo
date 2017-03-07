package com.noe.rxjava.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noe.rxjava.R;

import java.util.ArrayList;

import me.henrytao.smoothappbarlayout.SmoothAppBarLayout;
import me.henrytao.smoothappbarlayout.base.ObservableFragment;
import me.henrytao.smoothappbarlayout.base.Utils;


/**
 * Created by lijie24 on 2016/11/28.
 */

public class RecyclerPageFragment extends Fragment implements ObservableFragment {
    private Context mContext;
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    ArrayList<String> arrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private View mRootView;
    private View headerView;

    public static RecyclerPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RecyclerPageFragment pageFragment = new RecyclerPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mContext = getActivity();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        initView(mRootView);
        return mRootView;
    }

    private void initView(View view) {
        for (int i = 'A'; i <= 'Z'; i++) {
            arrayList.add(((char) i) + "");
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        headerView = View.inflate(mContext, R.layout.header_recycler, null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);


        MyRecyclerAdapter adapter = new MyRecyclerAdapter(arrayList);
        adapter.addHeaderView(headerView);
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public View getScrollTarget() {
        return mRecyclerView;
    }

    @Override
    public boolean onOffsetChanged(SmoothAppBarLayout smoothAppBarLayout, View target, int verticalOffset) {
        return Utils.syncOffset(smoothAppBarLayout, target, verticalOffset, getScrollTarget());
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<String> mArrayList;
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;
        private View mHeaderView;

        public MyRecyclerAdapter(ArrayList<String> arrayList) {
            this.mArrayList = arrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER) {
                return new ViewHolder(mHeaderView);
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void addHeaderView(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) {
                return TYPE_NORMAL;
            }
            if (position == 0) {
                return TYPE_HEADER;
            }
            return TYPE_NORMAL;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                return;
            }
            holder.mTextView.setText(arrayList.get(position - 1));
        }

        @Override
        public int getItemCount() {
            return mHeaderView == null ? mArrayList.size() : mArrayList.size() + 1;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            if (itemView == headerView) {
                return;
            }
            mTextView = (TextView) itemView.findViewById(R.id.tv_recycler);
        }
    }

}
