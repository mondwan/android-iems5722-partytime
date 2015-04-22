package com.iems5722.partytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class PatternActivity extends PortraitOnlyActivity {

    private static final String TAG = PatternActivity.class.getClass().getSimpleName();

    // GUI
    TextView p1ScoreView, p2ScoreView
            , p3ScoreView, p4ScoreView;
    TextView scoreView, timeView, instructionView;
    ImageButton up, down, left, right;

    // Game Var
    final int minArrowSize = 3;
    final int bufArrowSize = 3;
    final int gameTime = 20;
    final int scoreRate = 10;
    int score = 0;
    int targetArrowSize = 0;

    // Instance
    ScoresUtils scoresUtils = new ScoresUtils(TAG);
    ArrayList<String> targetArray = new ArrayList<String>();
    Boolean isFinish = false;

    private String genRandomArrow() {
        int action = new Random().nextInt(4);
        String ret = "";
        switch (action) {
            case 0:
                //Up
                ret = "up";
                break;
            case 1:
                //Down
                ret = "down";
                break;
            case 2:
                //Left
                ret = "left";
                break;
            case 3:
                //Right
                ret = "right";
                break;
            default:
                ret = "mondhaha";
                break;
        }
        return ret;
    }

    private void genAndSetArrow() {
        targetArray.clear();
        targetArrowSize = new Random().nextInt(minArrowSize) + bufArrowSize;

        for (int i = 0; i < targetArrowSize ; i++) {
            String newColor = genRandomArrow();
            while (i > 0 && newColor == targetArray.get(i - 1)) {
                newColor = genRandomArrow();
            }
            targetArray.add(newColor);
        }
    }

    private void showArrow() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetArray.size(); i++) {
            sb.append(targetArray.get(i) + " ");
        }
        instructionView.setText(sb.toString());
    }

    int checkCounter = 0;

    private void scoreUpdate(int diff) {
        score = scoresUtils.scoresUpdate(diff);
    }

    private Boolean checkInputArrow(String arrow) {
        Boolean ret = false;
        if (targetArray.get(checkCounter).equals(arrow)) {
            checkCounter++;
            if (checkCounter == targetArrowSize) {
                // success one
                scoreUpdate(scoreRate);
                scoreView.setText(Integer.toString(score));

                // reinit
                checkCounter = 0;
                genAndSetArrow();
                showArrow();
            }
            return true;
        } else {
            scoreUpdate(scoreRate * -1 / 2);
            scoreView.setText(Integer.toString(score));

            // reinit
            checkCounter = 0;
            genAndSetArrow();
            showArrow();
            return false;
        }
    }

    private void buttonChangeBG(final ImageButton button, final Boolean flag) {
        new CountDownTimer(100, 10) {

            public void onTick(long millisUntilFinished) {
                if (flag) {
                    button.setBackgroundColor(Color.GREEN);
                } else {
                    button.setBackgroundColor(Color.RED);
                }
            }

            public void onFinish() {
                button.setBackgroundColor(Color.TRANSPARENT);
            }
        }.start();
    }
    private void setScoreTable() {
        ArrayList<String> scoreList = scoresUtils.getSortedScoreText();

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
        setContentView(R.layout.activity_pattern);

        scoreView = (TextView) this.findViewById(R.id.scoreView);
        timeView = (TextView) this.findViewById(R.id.timeView);
        instructionView = (TextView) this.findViewById(R.id.instructionView);

        up = (ImageButton) this.findViewById(R.id.up);
        down = (ImageButton) this.findViewById(R.id.down);
        left = (ImageButton) this.findViewById(R.id.left);
        right = (ImageButton) this.findViewById(R.id.right);

        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);
        // init Screen
        genAndSetArrow();
        showArrow();

        up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("up");
                buttonChangeBG(up, flag);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("down");
                buttonChangeBG(down, flag);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("left");
                buttonChangeBG(left, flag);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("right");
                buttonChangeBG(right, flag);
            }
        });

        new CountDownTimer(gameTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText("Time remaining: " + millisUntilFinished / 1000);
                setScoreTable();
            }

            public void onFinish() {
                timeView.setText("Finish!");
                isFinish = true;
                scoreUpdate(0);
                up.setOnClickListener(null);
                down.setOnClickListener(null);
                left.setOnClickListener(null);
                right.setOnClickListener(null);

                // return score
                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        }.start();
    }
}
