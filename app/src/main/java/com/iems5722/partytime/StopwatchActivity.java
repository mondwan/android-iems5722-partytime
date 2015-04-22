package com.iems5722.partytime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class StopwatchActivity extends PortraitOnlyActivity {
    final String TAG = "Stopwatch";

    // GUI
    TextView timeView, scoreView, instructionView;
    Button stopButton;
    TextView p1ScoreView, p2ScoreView
            , p3ScoreView, p4ScoreView;

    // Game Var
    final int BUFFER_TIME = 5000;
    int targetTime = 5000; // in millis second
    int score = 0;

    // Instance
    protected GameController gameController = null;
    ScoresUtils scoresUtils = new ScoresUtils(TAG);

    private float getRemainTime(long untilFinished) {
        return (float) (targetTime - untilFinished + BUFFER_TIME) / 1000;
    }

    private int initRandtime() {
        int max = 6;
        int min = 3;

        Random random = new Random();
        targetTime = (random.nextInt(max - min) + min) * 1000;
        return targetTime;
    }

    String currentTime = "";

    private void setCurrentTime(String time) {
        this.currentTime = time;
    }

    private String getCurrentTime() {
        return currentTime;
    }

    private void scoreUpdate(int diff) {
        score = scoresUtils.scoresUpdate(diff);
    }

    private void setScoreTable() {
        ArrayList<String> scoreList = scoresUtils.getSortedScoreText();
        // p1
        try {
            p1ScoreView.setText(scoreList.get(0));
            p2ScoreView.setText(scoreList.get(1));
            p3ScoreView.setText(scoreList.get(2));
            p4ScoreView.setText(scoreList.get(3));
        } catch (Exception e) {
            // something overflow
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        timeView = (TextView) this.findViewById(R.id.timeView);
        scoreView = (TextView) this.findViewById(R.id.scoreView);
        stopButton = (Button) this.findViewById(R.id.stopButton);
        instructionView = (TextView) this.findViewById(R.id.instructionView);
        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);
        // init
        instructionView.setText("Stopped close to: " + initRandtime() / 1000);
        setScoreTable();

        String timeShow = "";
        final CountDownTimer cdtimer = new CountDownTimer(targetTime + BUFFER_TIME, 10) {

            public void onTick(long millisUntilFinished) {

                String timeShow = String.format("%.3f", getRemainTime(millisUntilFinished));
                setCurrentTime(timeShow);
                timeView.setText("Time: " + timeShow);
            }

            public void onFinish() {
                timeView.setText("End of World");

                Intent output = new Intent();
                score = 0;
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        };

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdtimer.cancel();
                timeView.setText(getCurrentTime());
                int diffToTarget = Math.abs((int) (Float.parseFloat(getCurrentTime())*1000) - targetTime);

                final int timeRatio = 500;
                score = timeRatio - diffToTarget;
                if (score < 0)
                    score = 0;
                else {
                    score = (int) ((score * 100) / timeRatio);
                }
                scoreUpdate(score);
                scoreView.setText("Score Board: " + score);

                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        });
        cdtimer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stopwatch, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
