package com.noe.rxjava;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.noe.rxjava.bean.DouBanMovieBean;
import com.noe.rxjava.bean.MovieDetailBean;
import com.noe.rxjava.data.DouBanApiService;
import com.noe.rxjava.data.DouBanRetroFactory;
import com.noe.rxjava.util.ArouterUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Route(path = ArouterUtils.ACTIVITY_DOUBAN)
public class DouBanActivity extends AppCompatActivity {


    @BindView(R.id.rv_douban)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douban);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Observable<DouBanMovieBean> observable = DouBanRetroFactory.createApi(DouBanActivity.this, DouBanApiService.class).getDouBanMovieBean();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DouBanMovieBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onNext(DouBanMovieBean douBanMovieBean) {
                        DouBanAdapter adapter = new DouBanAdapter(DouBanActivity.this, douBanMovieBean.subjects);
                        mRecyclerView.setLayoutManager(new GridLayoutManager(DouBanActivity.this, 3));
                        mRecyclerView.setAdapter(adapter);
                    }
                });
    }

    private class DouBanAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<MovieDetailBean> mSubjects;
        private LayoutInflater mInflater;
        private Context mContext;

        DouBanAdapter(Context context, ArrayList<MovieDetailBean> subjects) {
            this.mSubjects = subjects;
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.item_douban, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mSubjects != null && mSubjects.size() > 0) {
                MovieDetailBean movieDetailBean = mSubjects.get(position);
                if (movieDetailBean != null) {
                    holder.title.setText(movieDetailBean.title);
                    if (movieDetailBean.images != null) {
                        Glide.with(mContext).load(movieDetailBean.images.large).into(holder.cover);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mSubjects == null ? 0 : mSubjects.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_douban_cover)
        ImageView cover;
        @BindView(R.id.tv_douban_title)
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
