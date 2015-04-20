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

import java.util.ArrayList;


public class GameSequenceActivity extends ActionBarActivity {

    final String TAG = "GameSequence";
    final public static String SCORE_CODE = "SCORE_CODE";

    // 0 for CrazyClick
    // 1 for ColorResponse
    // 2 for Pattern
    // 3 for stopwatch
    final int maxGameNumber = 4;

    final int crazyGameRequestCode = 0;
    final int colorResponseCode = 1;
    final int patternCode = 2;
    final int stopwatchCode = 3;

    // Game flow control
    ArrayList<Integer> gameQueue;
    int gameIndex = 0;

    // GUI
    Button testButton;
    TextView scoreView;


    private void initGameQueue() {
        // hardcode
        // @TODO random unique game queue
        gameQueue = new ArrayList<Integer>();
        gameQueue.add(0);
        gameQueue.add(1);
        gameQueue.add(2);
        gameQueue.add(3);
    }

    private void startCrazyClickGame() {
        Intent intent = new Intent(GameSequenceActivity.this, CrazyClickActivity.class);
        startActivityForResult(intent, crazyGameRequestCode);
    }

    private void startColorResponseGame() {
        Intent intent = new Intent(GameSequenceActivity.this, ColorResponseActivity.class);
        startActivityForResult(intent, colorResponseCode);
    }

    private void startPatternGame() {
        Intent intent = new Intent(GameSequenceActivity.this, PatternActivity.class);
        startActivityForResult(intent, patternCode);
    }

    private void startStopwatchGame() {
        Intent intent = new Intent(GameSequenceActivity.this, StopwatchActivity.class);
        startActivityForResult(intent, stopwatchCode);
    }

    private void startCD() {
        Intent intent = new Intent(GameSequenceActivity.this, CountDownActivity.class);
        startActivity(intent);
    }

    private void startChosenGame(int index) {
        switch(index) {
            case 0:
                //CrazyClick
                startCrazyClickGame();
                break;
            case 1:
                //ColorResponse
                startColorResponseGame();
                break;
            case 2:
                //Pattern
                startPatternGame();
                break;
            case 3:
                //Stopwatch
                startStopwatchGame();
                break;
            default:
                break;
        }
    }

    private String getButtonText() {
        String ret = "";
        switch(gameIndex) {
            case 0:
                //CrazyClick
                ret = "CrazyClick";
                break;
            case 1:
                //ColorResponse
                ret = "ColorResponse";
                break;
            case 2:
                //Pattern
                ret = "Pattern";
                break;
            case 3:
                //Stopwatch
                ret = "Stopwatch";
                break;
            default:
                //Mondhaha Game
                ret = "Mond haha";
                break;
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sequence);

        scoreView = (TextView) this.findViewById(R.id.scoreView);
        testButton = (Button) this.findViewById(R.id.testButton);

        // init
        initGameQueue();
        testButton.setText("Click to start next game: " + getButtonText()) ;

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startChosenGame(gameQueue.get(gameIndex++));
                } catch (Exception e) {
                    gameIndex = 0;
                    startChosenGame(gameQueue.get(gameIndex++));
                }
                startCD();
                testButton.setText("Click to start next game: " + getButtonText()) ;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode == crazyGameRequestCode &&
        if (resultCode == RESULT_OK && data != null) {
            int score = data.getIntExtra(SCORE_CODE, 100);
            scoreView.setText("Score Board: " + Integer.toString(score));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_sequence, menu);
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
