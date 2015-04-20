package com.iems5722.partytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


public class ColorResponseActivity extends PortraitOnlyActivity {
    final String TAG = "ColorResponse";
    final int gameTime = 10;

    Button masterButton, redButton, blueButton, greenButton;
    TextView timeView, scoreView;
    Boolean isFinish = false;
    int targetColor = Color.RED;
    int score = 0;
    int correctCounter = 0;
    int incorrectCounter = 0;

    private void genAndSetColor() {
        Random rn = new Random();
        int color = rn.nextInt(3);
        switch (color) {
            case 0:
                //Red
                targetColor = Color.RED;
                masterButton.setBackgroundColor(Color.RED);
                break;
            case 1:
                //Green
                targetColor = Color.GREEN;
                masterButton.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                //Blue
                targetColor = Color.BLUE;
                masterButton.setBackgroundColor(Color.BLUE);
                break;
            default:
                //default
                break;
        }
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

    private Boolean checkColorCorrect(int color) {
        Boolean ret = false;
        if (targetColor == color) {
            correctCounter++;
            genAndSetColor();
            score++;
            ret = true;
        } else {
            incorrectCounter++;
            score--;
            ret = false;
        }
        scoreView.setText("Score: " + Integer.toString(score));
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_response);

        masterButton = (Button) this.findViewById(R.id.masterButton);
        redButton = (Button) this.findViewById(R.id.redButton);
        redButton.setBackgroundColor(Color.LTGRAY);
        greenButton = (Button) this.findViewById(R.id.greenButton);
        greenButton.setBackgroundColor(Color.LTGRAY);
        blueButton = (Button) this.findViewById(R.id.blueButton);
        blueButton.setBackgroundColor(Color.LTGRAY);
        timeView = (TextView) this.findViewById(R.id.timeView);
        scoreView = (TextView) this.findViewById(R.id.scoreView);

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
            }

            public void onFinish() {
                timeView.setText("Finish!");
                isFinish = true;
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
