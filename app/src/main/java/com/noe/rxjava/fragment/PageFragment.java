package com.noe.rxjava.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.noe.rxjava.R;
import com.noe.rxjava.view.NoScrollListView;

import java.util.ArrayList;


/**
 * Created by lijie24 on 2016/11/28.
 */

public class PageFragment extends Fragment {
    private Context mContext;
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    ArrayList<String> arrayList = new ArrayList<>();
    private NoScrollListView mListView;
    private View mRootView;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment pageFragment = new PageFragment();
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
        mRootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        initView(mRootView);
        return mRootView;
    }

    private void initView(View view) {
        String mString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < mString.length(); i++) {
            arrayList.add(String.valueOf(mString.charAt(i)));
        }
        mListView = (NoScrollListView) view.findViewById(R.id.list_fragment);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, arrayList);
        mListView.setAdapter(arrayAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getLastVisiblePosition() == view.getCount() - 1){
                    String mString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    for (int i = 0; i < mString.length(); i++) {
                        arrayList.add(String.valueOf(mString.charAt(i)));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
