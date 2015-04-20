package com.iems5722.partytime;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CrazyClickActivity extends ActionBarActivity {

    final int gameTime = 10;

    Button crazyButton;
    TextView counterView;
    TextView timeView;

    Boolean isFinish = false;
    int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_click);

        counterView = (TextView) this.findViewById(R.id.counterView);
        counterView.setText("0");

        timeView = (TextView) this.findViewById(R.id.timeView);

        crazyButton = (Button) this.findViewById(R.id.crazyButton);
        crazyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinish) {
                    clickCounter++;
                    counterView.setText(Integer.toString(clickCounter));
                }
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

                Intent output = new Intent();
                output.putExtra(GameSequenceActivity.SCORE_CODE, clickCounter);
                setResult(RESULT_OK, output);
                finish();
            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crazy_click, menu);
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
