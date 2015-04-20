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
        for (int i = 0; i < maxArrowSize; i++) {
            String newColor = genRandomArrow();
            while (i > 0 && newColor == targetArray.get(i-1)) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        scoreView = (TextView) this.findViewById(R.id.scoreView);
        timeView = (TextView) this.findViewById(R.id.timeView);
        instructionView = (TextView) this.findViewById(R.id.instructionView);

        upButton = (Button) this.findViewById(R.id.upButton); upButton.setBackgroundColor(Color.LTGRAY);
        downButton = (Button) this.findViewById(R.id.downButton); downButton.setBackgroundColor(Color.LTGRAY);
        leftButton = (Button) this.findViewById(R.id.leftButton); leftButton.setBackgroundColor(Color.LTGRAY);
        rightButton = (Button) this.findViewById(R.id.rightButton); rightButton.setBackgroundColor(Color.LTGRAY);

        // init Screen
        genAndSetArrow();
        showArrow();

        upButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("up");
                buttonChangeBG(upButton, flag);
            }
        });

        downButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("down");
                buttonChangeBG(downButton, flag);
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("left");
                buttonChangeBG(leftButton, flag);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Boolean flag = checkInputArrow("right");
                buttonChangeBG(rightButton, flag);
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
