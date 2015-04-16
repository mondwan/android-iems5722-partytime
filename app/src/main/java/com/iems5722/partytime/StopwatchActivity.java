package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

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
        mChronometer.setText("00:000");
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            public void onChronometerTick(Chronometer cArg){
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                Date date = new Date(time);
                DateFormat format = new SimpleDateFormat("ss:SSS");
                String dateformat = format.format(date);
                cArg.setText(dateformat);
            }
        });

        // Watch for button clicks.
        button = (Button) findViewById(R.id.start);
        button.setOnClickListener(mStartListener);

        button = (Button) findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);

        button = (Button) findViewById(R.id.reset);
        button.setOnClickListener(mResetListener);

    }

    private void showElapsedTime(String stoptime) {
        //long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
        Log.d(TAG, "stoptime: " + stoptime);
        String array[] = stoptime.split(":");
        int stoppedMilliseconds = Integer.parseInt(array[0]) * 1000 + Integer.parseInt(array[1]);
        Log.d(TAG, "stoppedMilliseconds: " + stoppedMilliseconds);
        int offset = 10000 / Math.abs(10 * 1000 - stoppedMilliseconds);
        Log.d(TAG, "offset: " + offset);
        int scores = Math.round(offset * 10);

        Toast.makeText(StopwatchActivity.this, "Your scores is " + scores,
                Toast.LENGTH_SHORT).show();

    }
    View.OnClickListener mStartListener = new View.OnClickListener() {
        public void onClick(View v) {
//            int stoppedMilliseconds = 0;
//            String chronoText = mChronometer.getText().toString();
//            String array[] = chronoText.split(":");
//            if (array.length == 2){
//                stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 +
//                        Integer.parseInt(array[1]) * 1000;
//            }else if (array.length == 3){
//                stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 +
//                        Integer.parseInt(array[1]) * 60 * 1000 +
//                        Integer.parseInt(array[2]) * 1000;
//            }
//            mChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
            mChronometer.start();
        }
    };

    View.OnClickListener mStopListener = new View.OnClickListener() {
        public void onClick(View v) {
            String stoptime = mChronometer.getText().toString();
            mChronometer.stop();
            showElapsedTime(stoptime);
        }
    };

    View.OnClickListener mResetListener = new View.OnClickListener() {
        public void onClick(View v) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setText("00:000");

        }
    };
}
