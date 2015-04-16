package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 16/4/15.
 * Simple StopWatch
 * */
public class StopwatchActivity extends Activity {
    private static final String TAG = StopwatchActivity.class.getClass().getSimpleName();
    Chronometer mChronometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        Button button;

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setText("00.000");
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            public void onChronometerTick(Chronometer cArg){
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                Date date = new Date(time);
                DateFormat format = new SimpleDateFormat("ss.SSS");
                String dateformat = format.format(date);
                cArg.setText(dateformat);
            }
        });

        // Watch for button clicks.
        button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Timer Started");
                mChronometer.start();
            }
        });

        button = (Button) findViewById(R.id.stop);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Timer Stopped");
                mChronometer.stop();
            }
        });

        button = (Button) findViewById(R.id.reset);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Timer Reset");
                mChronometer.setBase(SystemClock.elapsedRealtime());
            }
        });

    }
}
