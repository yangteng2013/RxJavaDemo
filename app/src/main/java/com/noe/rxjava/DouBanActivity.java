package com.noe.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.noe.rxjava.base.RxApplication;
import com.noe.rxjava.bean.DouBanMovieBean;
import com.noe.rxjava.data.DouBanApiService;
import com.noe.rxjava.data.DouBanRetroFactory;
import com.noe.rxjava.util.ArouterUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Route(path = ArouterUtils.ACTIVITY_DOUBAN)
public class DouBanActivity extends AppCompatActivity {

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douban);
        mTitle = (TextView) findViewById(R.id.tv_title);
        initData();
    }

    private void initData() {
        Observable<DouBanMovieBean> observable = DouBanRetroFactory.createApi(RxApplication.getInstance(), DouBanApiService.class).getDouBanMovieBean();
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
                        mTitle.setText(douBanMovieBean.subjects.get(0).collectCount + "");
                    }
                });
    }
}
