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

public class XRecyclerPageFragment extends Fragment implements ObservableFragment {
    private Context mContext;
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    ArrayList<String> arrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    public static XRecyclerPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        XRecyclerPageFragment pageFragment = new XRecyclerPageFragment();
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
        View mRootView = inflater.inflate(R.layout.fragment_xrecycler, container, false);
        initView(mRootView);
        return mRootView;
    }

    private void initView(View view) {
        for (int i = 'A'; i <= 'Z'; i++) {
            arrayList.add(((char) i) + "");
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);

        MyRecyclerAdapter adapter = new MyRecyclerAdapter(arrayList);
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

        public MyRecyclerAdapter(ArrayList<String> arrayList) {
            this.mArrayList = arrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(arrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_recycler);
        }
    }

}
