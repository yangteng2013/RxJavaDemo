package com.noe.rxjava;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    private class IntroPager extends PagerAdapter {
        private String[] mDescs;
        private TypedArray mIcons;
        public IntroPager(int icoImage, int des)
        {
            mDescs = getResources().getStringArray(des);
            mIcons = getResources().obtainTypedArray(icoImage);
        }

        @Override
        public int getCount()
        {
            return mIcons.length();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View itemLayout = getLayoutInflater().inflate(R.layout.viewpager_intro, container, false);
            TextView mTextView = (TextView)itemLayout.findViewById(R.id.tv_desc);
//            ImageView mImage = (ImageView)itemLayout.findViewById(R.id.iv_img);
            Button mButton = (Button)itemLayout.findViewById(R.id.btn_launch);
            mTextView.setText(mDescs[position]);
//            mImage.setImageResource(mIcons.getResourceId(position, 0));
            if (position == getCount() - 1)
            {
                mButton.setVisibility(View.VISIBLE);
            }
            else
            {
                mButton.setVisibility(View.GONE);
            }
            container.addView(itemLayout);
            return itemLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View)object);
        }
    }
}
