package com.iems5722.partytime;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
//import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Random;


/**
 * Created by Kevin on 16/4/15.
 * Simple StopWatch
 * */

 public class StopwatchActivity extends Activity {
    private static final String TAG = StopwatchActivity.class.getClass().getSimpleName();
    protected MediaPlayer mp, warn;
    long init,now,time,paused;
    TextView display;
    TextView rule;
    Handler handler;
    int randnum;
    int scores;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        final ToggleButton passTog = (ToggleButton) findViewById(R.id.onoff);
        display = (TextView) findViewById(R.id.display);
        rule = (TextView) findViewById(R.id.game_rule);
        handler = new Handler();
        randNumGen();
        rule.setText("Stop the timer after " + randnum + " seconds!");
        mp = MediaPlayer.create(StopwatchActivity.this,R.raw.ticktak);
        mp.setLooping(true);
        warn = MediaPlayer.create(StopwatchActivity.this,R.raw.warning);
        warn.setLooping(true);
        Log.d(TAG, "onCreate");



        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                if (passTog.isChecked()) {
                    mp.start();
                    now = System.currentTimeMillis();
                    time = now - init;
                    String displaymillisec = Long.toString(time);
                    String displaysec = "00";
                    int seconds;
                    if (time > 1000){
                        if(time > (randnum - 3) * 1000){
                            mp.setVolume(30,30);
                            warn.start();
                            display.setTextColor(Color.RED);
                        }

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
                Log.d(TAG, "Click received in onClick.");
                init = System.currentTimeMillis();
                handler.post(updater);
            }
        });


    }
    @Override
    protected void onPause() {
        Log.d(TAG, "Click received in onPause.");
        super.onPause();
        paused = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Click received in onResume.");
        super.onResume();
        init += System.currentTimeMillis() - paused;
    }
    private void showElapsedTime(Long stoptime) {
        warn.stop();
        mp.stop();
        Integer mstoptime = (int) (long) stoptime;
        Log.d(TAG, "stoppedMilliseconds: " + mstoptime);
        int offset = randnum * 1000 - Math.abs(randnum * 1000 - mstoptime);
        Log.d(TAG, "offset: " + offset);
        scores = Math.round(offset / randnum);
        Log.d(TAG, "scores: " + scores);
        Toast.makeText(StopwatchActivity.this, "Your scores is " + scores,
                Toast.LENGTH_SHORT).show();

    }

    private void randNumGen(){
        int max = 10;
        int min = 5;

        Random random = new Random();
        randnum = random.nextInt(max - min + 1) + min;

    }

}
