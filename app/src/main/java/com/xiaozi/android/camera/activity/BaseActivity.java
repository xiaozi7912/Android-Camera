package com.xiaozi.android.camera.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.xiaozi.android.camera.utils.Logger;

/**
 * Created by xiaoz on 2017-10-08.
 */

public class BaseActivity extends Activity {
    protected final String LOG_TAG = getClass().getSimpleName();
    protected Handler mHandler = new Handler();
    protected Activity mActivity = this;
    protected DisplayMetrics mDisplayMetrics = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplayMetrics = getResources().getDisplayMetrics();
    }

    protected void initialize() {

    }

    protected void initView() {

    }
}
