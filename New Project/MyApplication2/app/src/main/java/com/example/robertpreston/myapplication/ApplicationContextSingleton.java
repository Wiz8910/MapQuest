package com.example.robertpreston.myapplication;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Adam on 11/8/2015.
 */
//courtesy of lovely stack overflow
public class ApplicationContextSingleton {
    private static Activity gContext;

    public static void setContext( Activity activity) {
        gContext = activity;
    }

    public static Activity getActivity() {
        return gContext;
    }

    public static Context getContext() {
        return gContext;
    }
}
