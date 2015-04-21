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
    protected GameController gameController = null;
    final int gameTime = 10;

    Button crazyButton;
    TextView counterView;
    TextView timeView;

    Boolean isFinish = false;
    int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_click);

        counterView = (TextView) this.findViewById(R.id.counterView);
        counterView.setText("0");
        timeView = (TextView) this.findViewById(R.id.timeView);
        crazyButton = (Button) this.findViewById(R.id.crazyButton);

        this.gameController = GameController.getInstance();
        final GameController.UpdateScoresRequest req = new GameController.UpdateScoresRequest();
        final GameController.UpdateScoresResponse res = new GameController.UpdateScoresResponse();
        crazyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinish) {
                    score++;
                    counterView.setText(Integer.toString(score));
                    if (gameController.isHost()){
                        res.scores = score;
                        res.requestIP = gameController.localIP;
                        res.serverIP = gameController.getServerIP();
                        gameController.sendMsg(res);
                        gameController.setGamePlayerScores(res.serverIP,res.scores);
                        GamePlayer player = gameController.getGamePlayer(res.serverIP);
                        Log.d(TAG, String.format("Scores Update(Server self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
                    }
                    else{
                        req.scores = score;
                        req.requestIP = gameController.localIP;
                        gameController.sendMsg(req);
                        gameController.setGamePlayerScores(req.requestIP,req.scores);
                        GamePlayer player = gameController.getGamePlayer(req.requestIP);
                        Log.d(TAG, String.format("Scores Update(Client self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
                    }
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
