package com.iems5722.partytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class StopwatchActivity extends PortraitOnlyActivity {
    final String TAG = "Stopwatch";

    // GUI
    protected TextView timeView, scoreView, instructionView;
    protected Button stopButton;
    protected TextView p1ScoreView, p2ScoreView, p3ScoreView, p4ScoreView;

    // Game Var
    protected static final int BUFFER_TIME = 5000; // in ms
    protected static final int UPDATE_INTERVAL = 10; // in ms
    protected static final int STOPWATCH_TIMER_UPPER_BOUND = 6; // in s
    protected static final int STOPWATCH_TIMER_LOWER_BOUND = 3; // in s
    protected static int targetTime; // in ms
    protected int score = 0;

    // Instance
    ScoresUtils scoresUtils = new ScoresUtils(TAG);

    /**
     * Helper method calculates how much time left
     *
     * @param untilFinished milliseconds
     * @return float
     */
    private float getTimeLeft(long untilFinished) {
        return (float) (targetTime - untilFinished + BUFFER_TIME) / 1000;
    }

    /**
     * Helper method generates a random stop time
     *
     * @return int milliseconds
     */
    private int getRandomStoptime() {
        int max = STOPWATCH_TIMER_UPPER_BOUND;
        int min = STOPWATCH_TIMER_LOWER_BOUND;

        // Generate a random number between max and min
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

        // Setup reference for our view element
        timeView = (TextView) this.findViewById(R.id.timeView);
        scoreView = (TextView) this.findViewById(R.id.scoreView);
        stopButton = (Button) this.findViewById(R.id.stopButton);
        instructionView = (TextView) this.findViewById(R.id.instructionView);
        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);

        // Initialize stopwatch variable
        instructionView.setText(
                "Try your best to stop close to: " + getRandomStoptime() / 1000);

        // Display scores for all players
        this.setScoreTable();

        // Define a count down timer for updating our UI
        final StopwatchActivity self = StopwatchActivity.this;
        final CountDownTimer countDownTimer =
                new CountDownTimer(targetTime + BUFFER_TIME, UPDATE_INTERVAL) {
                    public void onTick(long millisUntilFinished) {
                        // Calculate how much time left
                        float timeLeft = self.getTimeLeft(millisUntilFinished);

                        // Converts to string
                        String timeShow = String.format("%.3f", timeLeft);
                        setCurrentTime(timeShow);

                        int diffToTarget = Math.abs(
                                (int) (Float.parseFloat(getCurrentTime()) * 1000) - targetTime);
                        if (diffToTarget < 1000) timeView.setTextColor(Color.RED);

                        timeView.setText("Time: " + timeShow);
                    }

                    public void onFinish() {
                        timeView.setText("End of World");

                        Intent output = new Intent();

                        // Return to GameSequence with score
                        score = 0;
                        output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                        setResult(RESULT_OK, output);

                        // kill myself
                        self.finish();
                    }
                };

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop timer
                countDownTimer.cancel();

                // Show time player stopping at
                timeView.setText(getCurrentTime());

                // Calculate difference between target time and stopping time
                final int diffToTarget = Math.abs(
                        (int) (Float.parseFloat(getCurrentTime()) * 1000) - targetTime);

                final int timeRatio = 500;

                // Calculate player score
                score = timeRatio - diffToTarget;
                if (score < 0) {
                    score = 0;
                } else {
                    score = (score * 100) / timeRatio;
                }

                // Update the player score
                scoreView.setText("Score Board: " + score);

                // Send message to server
                scoreUpdate(score);

                // Return to GameSequence with player score
                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);

                // kill myself
                self.finish();
            }
        });

        // Start the stopwatch game
        countDownTimer.start();
    }
}
