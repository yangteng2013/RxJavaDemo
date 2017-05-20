package com.noe.rxjava;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.noe.rxjava.fragment.XRecyclerPageFragment;
import com.noe.rxjava.util.ArouterUtils;

@Route(path = ArouterUtils.ACTIVITY_XRECYCLER)
public class XRecyclerActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Context mContext;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_xrecycler);
        initView();

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.toolbar_tab);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mViewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mAppBarLayout,this));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{"最热", "最新"};
        private AppBarLayout mAppBarLayout;
        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm, AppBarLayout appBarLayout, Context context) {
            super(fm);
            this.context = context;
            this.mAppBarLayout = appBarLayout;
        }

        @Override
        public Fragment getItem(int position) {
            return XRecyclerPageFragment.newInstance(position + 1,mAppBarLayout);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
