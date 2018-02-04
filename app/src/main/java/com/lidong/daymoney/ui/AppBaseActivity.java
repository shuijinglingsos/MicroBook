package com.lidong.daymoney.ui;

import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * activity 基类
 * Created by Lidong on 2018/2/4.
 */
public class AppBaseActivity extends AppCompatActivity {


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
