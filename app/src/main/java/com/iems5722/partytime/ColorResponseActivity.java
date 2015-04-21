package com.iems5722.partytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


public class ColorResponseActivity extends PortraitOnlyActivity {
    final String TAG = "ColorResponse";
    protected GameController gameController = null;
    final int gameTime = 20;

    Button redButton, blueButton, greenButton;
    TextView displayView, timeView, scoreView;
    Boolean isFinish = false;
    int targetColor = Color.RED;
    int score = 0;
    int correctCounter = 0;
    int incorrectCounter = 0;

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
        score += diff;
        this.gameController = GameController.getInstance();
        final GameController.UpdateScoresRequest req = new GameController.UpdateScoresRequest();
        final GameController.UpdateScoresResponse res = new GameController.UpdateScoresResponse();
        if (gameController.isHost()){
            GamePlayer player = gameController.getGamePlayer(gameController.localIP);
            res.scores = player.getScores() + diff;
            res.requestIP = gameController.localIP;
            res.serverIP = gameController.getServerIP();
            gameController.sendMsg(res);
            gameController.setGamePlayerScores(res.serverIP,res.scores);


            Log.d(TAG, String.format("Scores Update(Server self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
        }
        else{
            GamePlayer player = gameController.getGamePlayer(gameController.localIP);
            res.scores = player.getScores() + diff;
            req.requestIP = gameController.localIP;
            gameController.sendMsg(req);
            gameController.setGamePlayerScores(req.requestIP,req.scores);
            Log.d(TAG, String.format("Scores Update(Client self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
        }

    }

    private Boolean checkColorCorrect(int color) {
        Boolean ret = false;
        if (targetColor == color) {
            correctCounter++;
            genAndSetColor();
            scoreUpdate(1);
            ret = true;
        } else {
            incorrectCounter++;
            scoreUpdate(-1);
            ret = false;
        }
        scoreView.setText("Score: " + Integer.toString(score));
        return ret;
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
