package com.iems5722.partytime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class CrazyClickActivity extends PortraitOnlyActivity {
    private static final String TAG = CrazyClickActivity.class.getClass().getSimpleName();

    // GUI
    Button crazyButton;
    TextView counterView;
    TextView timeView;
    TextView p1ScoreView, p2ScoreView
            , p3ScoreView, p4ScoreView;

    // Game Var
    final int gameTime = 5;
    final int scoreRate = 1;

    // Game Flow
    Boolean isFinish = false;
    int score = 0;

    // Instance
    ScoresUtils scoresUtils = new ScoresUtils(TAG);

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
        setContentView(R.layout.activity_crazy_click);

        counterView = (TextView) this.findViewById(R.id.counterView);
        counterView.setText("0");
        timeView = (TextView) this.findViewById(R.id.timeView);
        crazyButton = (Button) this.findViewById(R.id.crazyButton);

        // TOCOPY
        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);

        crazyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinish) {
                    scoreUpdate(scoreRate);
                    counterView.setText(Integer.toString(score));
                }
            }
        });

        // Game Start
        new CountDownTimer(gameTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText("Time remaining: " + millisUntilFinished / 1000);
                setScoreTable();
            }

            public void onFinish() {
                timeView.setText("Finish!");
                isFinish = true;
                scoreUpdate(0);

                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        }.start();
    }
}
