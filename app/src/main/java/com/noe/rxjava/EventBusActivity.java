package com.noe.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 58 on 2016/8/24. eventbus 3.0
 */
public class EventBusActivity extends AppCompatActivity{

    private Button btnEvent;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        btnEvent = (Button) findViewById(R.id.btnEvent);
        mTextView = (TextView) findViewById(R.id.text);


        btnEvent.setOnClickListener(v -> EventBus.getDefault().post("hello"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
