package com.iems5722.partytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class ColorResponseActivity extends PortraitOnlyActivity {
    private static final String TAG = ColorResponseActivity.class.getClass().getSimpleName();
    ScoresUtils scoresUtils = new ScoresUtils(TAG);

    // GUI
    Button redButton, blueButton, greenButton;
    TextView displayView, timeView, scoreView;

    // Game Flow
    Boolean isFinish = false;
    int targetColor = Color.RED;

    // Game Var
    final int gameTime = 15;
    final int scoreRate = 9;
    int score = 0;
    int correctCounter = 0;
    int incorrectCounter = 0;

    // TOCOPY
    TextView p1ScoreView, p2ScoreView
            , p3ScoreView, p4ScoreView;

    final String COLOR_TEXT[] = new String[] {
      "RED", "GREEN", "BLUE"
    };

    private void genAndSetColor() {
        Random rn = new Random();
        int color = rn.nextInt(COLOR_TEXT.length);
        int randText = rn.nextInt(COLOR_TEXT.length);
        switch (color) {
            case 0:
                //Red
                targetColor = Color.RED;
                displayView.setTextColor(Color.RED);
                break;
            case 1:
                //Green
                targetColor = Color.GREEN;
                displayView.setTextColor(Color.GREEN);
                break;
            case 2:
                //Blue
                targetColor = Color.BLUE;
                displayView.setTextColor(Color.BLUE);
                break;
            default:
                //default
                break;
        }
        displayView.setText(COLOR_TEXT[randText]);
    }

    private void buttonChangeBG(final Button button, final Boolean flag) {
        new CountDownTimer(100, 10) {

            public void onTick(long millisUntilFinished) {
                if (flag) {
                    button.setBackgroundColor(Color.GREEN);
                } else {
                    button.setBackgroundColor(Color.RED);
                }
            }

            public void onFinish() {
                button.setBackgroundColor(Color.LTGRAY);
            }
        }.start();
    }

    private void scoreUpdate(int diff) {
        score = scoresUtils.scoresUpdate(diff);
    }

    private Boolean checkColorCorrect(int color) {
        Boolean ret = false;
        if (targetColor == color) {
            correctCounter++;
            genAndSetColor();
            scoreUpdate(scoreRate);
            ret = true;
        } else {
            incorrectCounter++;
            scoreUpdate(scoreRate * -1 / 2);
            ret = false;
        }
        scoreView.setText("Score: " + Integer.toString(score));
        return ret;
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
        setContentView(R.layout.activity_color_response);

        redButton = (Button) this.findViewById(R.id.redButton);
        redButton.setBackgroundColor(Color.LTGRAY);
        greenButton = (Button) this.findViewById(R.id.greenButton);
        greenButton.setBackgroundColor(Color.LTGRAY);
        blueButton = (Button) this.findViewById(R.id.blueButton);
        blueButton.setBackgroundColor(Color.LTGRAY);
        timeView = (TextView) this.findViewById(R.id.timeView);
        displayView = (TextView) this.findViewById(R.id.displayView);
        scoreView = (TextView) this.findViewById(R.id.scoreView);

        // TOCOPY
        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);

        genAndSetColor();
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.RED;
                buttonChangeBG(redButton, checkColorCorrect(color));
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.GREEN;
                buttonChangeBG(greenButton, checkColorCorrect(color));
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.BLUE;
                buttonChangeBG(blueButton, checkColorCorrect(color));
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
                redButton.setOnClickListener(null);
                greenButton.setOnClickListener(null);
                blueButton.setOnClickListener(null);

                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        }.start();
    }
}
