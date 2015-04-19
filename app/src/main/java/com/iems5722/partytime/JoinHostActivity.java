package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;


public class JoinHostActivity extends Activity {
    private static final String TAG = JoinHostActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_host);
    }
}
