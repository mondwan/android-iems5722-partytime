package com.iems5722.partytime;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class ColorResponseActivity extends ActionBarActivity {
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
        Log.d(TAG, Integer.toString(color));
        switch(color) {
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

    private void checkColorCorrect(int color) {
        if (targetColor == color) {
            correctCounter++;
            genAndSetColor();
            score++;
        } else {
            incorrectCounter++;
            score--;
        }
        scoreView.setText("Score: " + Integer.toString(score));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_response);

        masterButton = (Button) this.findViewById(R.id.masterButton);
        redButton = (Button) this.findViewById(R.id.redButton);
        greenButton = (Button) this.findViewById(R.id.greenButton);
        blueButton = (Button) this.findViewById(R.id.blueButton);
        timeView = (TextView) this.findViewById(R.id.timeView);
        scoreView = (TextView) this.findViewById(R.id.scoreView);

        genAndSetColor();
        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.RED;
                checkColorCorrect(color);
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.GREEN;
                checkColorCorrect(color);
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = Color.BLUE;
                checkColorCorrect(color);
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
            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color_response, menu);
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
