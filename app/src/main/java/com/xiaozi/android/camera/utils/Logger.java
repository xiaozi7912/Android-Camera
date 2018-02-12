package com.xiaozi.android.camera.utils;

import android.util.Log;

/**
 * Created by xiaoz on 2017-10-08.
 */

public class Logger {
    private static boolean mDebug = false;

    public static void init(boolean debug) {
        mDebug = debug;
    }

    public static void i(String tag, String message) {
        if (mDebug) Log.i(tag, message);
    }

    public static void d(String tag, String message) {
        if (mDebug) Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        if (mDebug) Log.e(tag, message);
    }
}
