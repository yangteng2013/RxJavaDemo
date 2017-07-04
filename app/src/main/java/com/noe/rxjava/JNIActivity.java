package com.noe.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.noe.rxjava.util.ArouterUtils;

@Route(path = ArouterUtils.ACTIVITY_JNI)
public class JNIActivity extends AppCompatActivity {
    private TextView mTextView;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);
        mTextView = (TextView) findViewById(R.id.tv_jni);
        mTextView.setText(stringFromJNI());
    }

    private native String stringFromJNI();
}
