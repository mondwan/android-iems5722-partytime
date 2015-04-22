package com.iems5722.partytime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class GameSequenceActivity extends PortraitOnlyActivity {

    final String TAG = "GameSequence";
    final public static String SCORE_CODE = "SCORE_CODE";

    protected GameController gameController = null;
    protected GameControllerHandler mHandler = null;
    ScoresUtils scoresUtils = new ScoresUtils(TAG);

    // 0 for CrazyClick
    // 1 for ColorResponse
    // 2 for Pattern
    // 3 for stopwatch
    final int maxGameNumber = 4;

    final static int crazyGameCode = 0;
    final static int colorResponseCode = 1;
    final static int patternCode = 2;
    final static int stopwatchCode = 3;

    // Game flow control
    ArrayList<Integer> gameQueue;
    public static int gameIndex = 0;

    // GUI
    Button nextGameButton;
    TextView scoreView;

    TextView p1ScoreView, p2ScoreView
            , p3ScoreView, p4ScoreView;

    /**
     * Handler for receiving events from GameController
     */
    protected static class GameControllerHandler extends Handler {
        protected final WeakReference<GameSequenceActivity> gameSequenceActivity;

        public GameControllerHandler(GameSequenceActivity gameSequenceActivity) {
            this.gameSequenceActivity = new WeakReference<>(gameSequenceActivity);
        }

        /**
         * Defines the operations to perform when this activity receives a new Message from the
         * GameController.
         *
         * @param inputMessage Message
         */
        @Override
        public void handleMessage(Message inputMessage) {
            final GameSequenceActivity self = this.gameSequenceActivity.get();

            switch (inputMessage.what) {
                case GameController.NEXT_GAME_NOTIFICATION:
                    self.startNextGame();
                    break;
                case GameController.SERVER_DOWN_NOTFICATION:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(self);
                    dialog.setTitle("Lobby closed");
                    dialog.setMessage(
                            "Lobby has been closed by the host."
                    );
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Back to HomeActivity
                                    self.finish();
                                }
                            }
                    );

                    dialog.show();
                    break;
            }
        }
    }

    private void initGameQueue() {
        // hardcode
        // @TODO random unique game queue
        gameQueue = new ArrayList<Integer>();
        gameQueue.add(crazyGameCode);
        gameQueue.add(colorResponseCode);
        gameQueue.add(patternCode);
        gameQueue.add(stopwatchCode);
    }

    private void startCrazyClickGame() {
        Intent intent = new Intent(GameSequenceActivity.this, CrazyClickActivity.class);
        startActivityForResult(intent, crazyGameCode);
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

    /**
     * API for others to start next game
     */
    public void startNextGame() {
        // Get next game index
        int index = this.getNextGame();

        if (index != -1) {
            // Jump to next Game
            this.startChosenGame(index);
            this.startCD();
        } else {
            Log.d(TAG, "No more game available");
            // Change text
            this.nextGameButton.setText(R.string.end_game);

            // Stop the GameServer if it is running
            this.gameController.stopGameServer();

            // Kill myself if there are no more game available
            this.finish();
        }
    }

    /**
     * Return the next game's index
     * @return int -1 indicate errors
     */
    private int getNextGame() {
        int index = gameIndex;
        int ret;

        if (index == this.maxGameNumber) {
            ret = -1;
        } else {
            ret = gameQueue.get(index);
            gameIndex++;
        }

        return ret;
    }

    private void startChosenGame(int index) {
        switch (index) {
            case crazyGameCode:
                //CrazyClick
                startCrazyClickGame();
                break;
            case colorResponseCode:
                //ColorResponse
                startColorResponseGame();
                break;
            case patternCode:
                //Pattern
                startPatternGame();
                break;
            case stopwatchCode:
                //Stopwatch
                startStopwatchGame();
                break;
            default:
                break;
        }
    }

    public static String getButtonText() {
        String ret = "";
        switch (gameIndex - 1) {
            case crazyGameCode:
                //CrazyClick
                ret = "Craz yClick Game";
                break;
            case colorResponseCode:
                //ColorResponse
                ret = "Color Response Game";
                break;
            case patternCode:
                //Pattern
                ret = "Pattern Game";
                break;
            case stopwatchCode:
                //Stopwatch
                ret = "Stopwatch Game";
                break;
            default:
                //Mondhaha Game
                ret = "Mond haha forever~!~";
                break;
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sequence);

        scoreView = (TextView) this.findViewById(R.id.scoreView);
        nextGameButton = (Button) this.findViewById(R.id.nextGameButton);

        // TOCOPY
        p1ScoreView = (TextView) this.findViewById(R.id.p1ScoreView);
        p2ScoreView = (TextView) this.findViewById(R.id.p2ScoreView);
        p3ScoreView = (TextView) this.findViewById(R.id.p3ScoreView);
        p4ScoreView = (TextView) this.findViewById(R.id.p4ScoreView);

        // Get gameController reference
        this.gameController = GameController.getInstance();

        // init
        initGameQueue();

        // For inner class reference
        final GameSequenceActivity self = this;

        // Register a handler for GameController to feedback event from GameServer
        this.mHandler = new GameControllerHandler(this);
        this.gameController.registerHandler(this.mHandler);

        if (this.gameController.isHost()) {
            nextGameButton.setText(R.string.next_game);
            nextGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // broadcast a StartGameNotification to others
                    GameController.NextGameNotification notification =
                            new GameController.NextGameNotification();
                    self.gameController.sendMsg(notification);

                    // Jump to GameSequenceActivity
                    self.startNextGame();
                }
            });
        } else {
            nextGameButton.setText(R.string.wait_for_host);
            nextGameButton.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode == crazyGameRequestCode &&
        if (resultCode == RESULT_OK && data != null) {
//            int score = data.getIntExtra(SCORE_CODE, 100);
            this.gameController = GameController.getInstance();
            final GameController.UpdateScoresRequest req = new GameController.UpdateScoresRequest();
            final GameController.UpdateScoresResponse res = new GameController.UpdateScoresResponse();
            GamePlayer player = gameController.getGamePlayer(gameController.localIP);
            if (gameController.isHost()){
                res.scores = player.getScores();
                res.requestIP = gameController.localIP;
                res.serverIP = gameController.getServerIP();
                gameController.setGamePlayerScores(res.serverIP,res.scores);

                String username = player.username;
                int scores = player.scores;
                Log.d(TAG, String.format("Scores Update(Server self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
                scoreView.setText("Player: " + username);
            }
            else{
                req.scores = player.getScores();
                req.requestIP = gameController.localIP;
                gameController.sendMsg(req);
                gameController.setGamePlayerScores(req.requestIP,req.scores);
                Log.d(TAG, String.format("Scores Update(Client self): Player |%s|, Scores|%s|", player.getUsername(), player.getScores()));
                String username = player.username;
                int scores = player.scores;
                scoreView.setText("Player: " + username);
            }

            setScoreTable();
        }
    }
}
