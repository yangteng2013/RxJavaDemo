package com.noe.rxjava;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 58 on 2016/8/24. 分发页面
 */
public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;
    private Button btn10;
    private Button btn11;
    private Button btn12;
    private Button btn13;
    private Button btn14;
    private Button btn15;
    private Button btn16;
    private Button btn17;
    private Button btn18;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_second);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);
        btn9 = (Button) findViewById(R.id.btn9);
        btn10 = (Button) findViewById(R.id.btn10);
        btn11 = (Button) findViewById(R.id.btn11);
        btn12 = (Button) findViewById(R.id.btn12);
        btn13 = (Button) findViewById(R.id.btn13);
        btn14 = (Button) findViewById(R.id.btn14);
        btn15 = (Button) findViewById(R.id.btn15);
        btn16 = (Button) findViewById(R.id.btn16);
        btn17 = (Button) findViewById(R.id.btn17);
        btn18 = (Button) findViewById(R.id.btn18);


        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
        btn12.setOnClickListener(this);
        btn13.setOnClickListener(this);
        btn14.setOnClickListener(this);
        btn15.setOnClickListener(this);
        btn16.setOnClickListener(this);
        btn17.setOnClickListener(this);
        btn18.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                Intent intent = new Intent(SecondActivity.this, EventBusActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn2:
                try {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(SecondActivity.this, "不支持定位", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn3:
                Bundle bundle = new Bundle();
                bundle.putString("ha", "haha");
                Intent intent1 = new Intent(SecondActivity.this, GridViewActivity.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.btn4:
                startActivity(new Intent(SecondActivity.this, ViewPagerActivity.class));
                break;
            case R.id.btn5:
                startActivity(new Intent(SecondActivity.this, SmoothBarActivity.class));
                break;
            case R.id.btn6:
                startActivity(new Intent(SecondActivity.this, RecyclerActivity.class));
                break;
            case R.id.btn7:
                startActivity(new Intent(SecondActivity.this, StaggeredGridActivity.class));
                break;
            case R.id.btn8:
                Intent intent2 = getPackageManager().getLaunchIntentForPackage("com.ganji.android");
                intent2.setData(Uri.parse("ganji://postdetail?data=%7B%22url%22%3A%22postdetail%22%2C%22cParam%22%3A%7B%22fromurl%22%3A%22http%3A%2F%2F3g.ganji.com%2Fbj_zpjigongyibangongren%2F99095303x%3Ffrom%3Dsinglemessage%22%2C%22from%22%3A%22weixin%22%2C%22fromposition%22%3A%22wap%22%7D%2C%22bParam%22%3A%7B%22category_id%22%3A%222%22%2C%22puid%22%3A%2299095303%22%7D%2C%22root_url%22%3A%22http%3A%2F%2Fsta.ganji.com%2Fatt%2Fproject%2Ftouch%2Fdownload_app%2Findex.html%3F%22%2C%22arg%22%3A%22http%3A%2F%2Fganji.cn%2Ft%2Fedetz9%22%2C%22schemaParm%22%3A%22ganji%3A%2F%2Fpostdetail%3F%22%2C%22other_url%22%3A%22https%3A%2F%2Fapplesite.ganji.com%22%7D"));
                startActivity(intent2);
                finish();
                break;
            case R.id.btn9:
                Intent intent3 = new Intent(Intent.ACTION_MAIN);
                intent3.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.ganji.android", "com.ganji.android.wxapi.WXEntryActivity");
                intent3.setComponent(cn);
                intent3.setData(Uri.parse("ganji://postdetail?data=%7B%22url%22%3A%22postdetail%22%2C%22cParam%22%3A%7B%22fromurl%22%3A%22http%3A%2F%2F3g.ganji.com%2Fbj_zpjigongyibangongren%2F99095303x%3Ffrom%3Dsinglemessage%22%2C%22from%22%3A%22weixin%22%2C%22fromposition%22%3A%22wap%22%7D%2C%22bParam%22%3A%7B%22category_id%22%3A%222%22%2C%22puid%22%3A%2299095303%22%7D%2C%22root_url%22%3A%22http%3A%2F%2Fsta.ganji.com%2Fatt%2Fproject%2Ftouch%2Fdownload_app%2Findex.html%3F%22%2C%22arg%22%3A%22http%3A%2F%2Fganji.cn%2Ft%2Fedetz9%22%2C%22schemaParm%22%3A%22ganji%3A%2F%2Fpostdetail%3F%22%2C%22other_url%22%3A%22https%3A%2F%2Fapplesite.ganji.com%22%7D"));
                startActivity(intent3);
                finish();
                break;
            case R.id.btn10:
                startActivity(new Intent(SecondActivity.this, DaggerActivity.class));
                break;
            case R.id.btn11:
                Intent intentCamera = new Intent(); //调用照相机
                intentCamera.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intentCamera);
                break;
            case R.id.btn12:
                Toast.makeText(SecondActivity.this, getVersionName(SecondActivity.this), Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn13:
                startActivity(new Intent(SecondActivity.this, VLayoutActivity.class));
                break;
            case R.id.btn14:
                startActivity(new Intent(SecondActivity.this, ConstraintLayoutActivity.class));
                break;
            case R.id.btn15:
                startActivity(new Intent(SecondActivity.this, GradientActivity.class));
                break;
            case R.id.btn16:
                break;
            case R.id.btn17:
                break;
            case R.id.btn18:
                break;
            default:
                break;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String msg) {
        btn1.setText(msg);
    }
}
