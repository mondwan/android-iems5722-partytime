package com.iems5722.partytime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CrazyClickActivity extends PortraitOnlyActivity {
    private static final String TAG = CrazyClickActivity.class.getClass().getSimpleName();
    ScoresUtils scoresUtils = new ScoresUtils(TAG);
    final int gameTime = 10;

    Button crazyButton;
    TextView counterView;
    TextView timeView;

    Boolean isFinish = false;
    int score = 0;

    private void scoreUpdate(int diff) {
        score = scoresUtils.ScoresUpdate(diff);

    }

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
                    scoreUpdate(1);
                    counterView.setText(Integer.toString(score));
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
                output.putExtra(GameSequenceActivity.SCORE_CODE, score);
                setResult(RESULT_OK, output);
                finish();
            }
        }.start();
    }
}
