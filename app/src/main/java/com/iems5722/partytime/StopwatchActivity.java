package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * Created by Kevin on 16/4/15.
 * Simple StopWatch
 * */

 public class StopwatchActivity extends Activity {
    private static final String TAG = StopwatchActivity.class.getClass().getSimpleName();
    long init,now,time,paused;
    TextView display;
    Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        final ToggleButton passTog = (ToggleButton) findViewById(R.id.onoff);
        display = (TextView) findViewById(R.id.display);
        handler = new Handler();
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                if (passTog.isChecked()) {
                    now = System.currentTimeMillis();
                    time = now - init;
                    String displaymillisec = Long.toString(time);
                    String displaysec = "00";
                    int seconds;
                    if (time > 1000){
                        seconds = (int) (time / 1000) % 60 ;
                        displaymillisec = Long.toString(time);
                        displaymillisec = displaymillisec.substring(displaymillisec.length() - 3);
                        displaysec = Integer.toString(seconds);
                        if (seconds < 10){
                            displaysec = "0" + displaysec;
                        }

                    }

                    display.setText(displaysec + "." + displaymillisec);
                    handler.postDelayed(this, 30);
                }
                else{
                    showElapsedTime(time);
                }
            }
        };
        passTog.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                init = System.currentTimeMillis();
                handler.post(updater);
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        paused = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init += System.currentTimeMillis() - paused;
    }
    private void showElapsedTime(Long stoptime) {
        //long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
        Integer mstoptime = (int) (long) stoptime;
        Log.d(TAG, "stoppedMilliseconds: " + mstoptime);
        int offset = 10000 - Math.abs(10 * 1000 - mstoptime);
        Log.d(TAG, "offset: " + offset);
        int scores = Math.round(offset / 10);

        Toast.makeText(StopwatchActivity.this, "Your scores is " + scores,
                Toast.LENGTH_SHORT).show();

    }
}
