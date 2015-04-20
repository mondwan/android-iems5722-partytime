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


public class PatternActivity extends ActionBarActivity {

    final String TAG = "Pattern Activity";
    final int gameTime = 10;

    TextView scoreView, timeView, instructionView;
    Button upButton, downButton, leftButton, rightButton;

    final int maxArrowSize = 5;
    int score = 0;
    ArrayList<String> targetArray = new ArrayList<String>();
    Boolean isFinish = false;

    private String genRandomArrow() {
        int action = new Random().nextInt(4);
        String ret = "";
        switch(action) {
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
        Random rn = new Random();
        int color = rn.nextInt(3);
        Log.d(TAG, Integer.toString(color));
        for (int i = 0; i < maxArrowSize; i++) {
            targetArray.add(genRandomArrow());
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
    private Boolean checkInputArrow(String arrow) {
        Boolean ret = false;
        if (targetArray.get(checkCounter).equals(arrow)) {
            checkCounter++;
            if (checkCounter == maxArrowSize) {
                // success one
                score++;
                scoreView.setText(Integer.toString(score));

                // reinit
                checkCounter = 0;
                genAndSetArrow();
                showArrow();
            }
            return true;
        } else {
            score--;
            scoreView.setText(Integer.toString(score));

            // reinit
            checkCounter = 0;
            genAndSetArrow();
            showArrow();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        scoreView = (TextView) this.findViewById(R.id.scoreView);
        timeView = (TextView) this.findViewById(R.id.timeView);
        instructionView = (TextView) this.findViewById(R.id.instructionView);

        upButton = (Button) this.findViewById(R.id.upButton);
        downButton = (Button) this.findViewById(R.id.downButton);
        leftButton = (Button) this.findViewById(R.id.leftButton);
        rightButton = (Button) this.findViewById(R.id.rightButton);

        // init Screen
        genAndSetArrow();
        showArrow();

        upButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkInputArrow("up");
            }
        });

        downButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkInputArrow("down");
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkInputArrow("left");
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                checkInputArrow("right");
            }
        });

        new CountDownTimer(gameTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText("Time remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timeView.setText("Finish!");
                isFinish = true;
                upButton.setOnClickListener(null);
                downButton.setOnClickListener(null);
                leftButton.setOnClickListener(null);
                rightButton.setOnClickListener(null);
            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pattern, menu);
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
