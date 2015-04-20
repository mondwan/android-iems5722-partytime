package com.iems5722.partytime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class LobbyActivity extends PortraitOnlyActivity {
    private static final String TAG = LobbyActivity.class.getClass().getSimpleName();

    protected Button startButton = null;
    protected TextView hostIP = null;
    protected ListView playerList = null;

    protected PlayerItemAdapter playerListAdapter = null;

    protected GameController gameController = null;

    // Handler reference
    protected Handler mHandler = null;

    // Media player reference
    protected MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Setup reference for our properties
        this.startButton = (Button) this.findViewById(R.id.startButton);
        this.hostIP = (TextView) this.findViewById(R.id.hostIP);
        this.playerList = (ListView) this.findViewById(R.id.playerList);

        // Get GameController
        this.gameController = GameController.getInstance();

        // Update player list by linking up with GameServer players and our adapter
        ArrayList<GamePlayer> players = this.gameController.getPlayerList();

        // Define PlayerAdapter
        this.playerListAdapter = new PlayerItemAdapter(this, R.layout.player_row_view, players);

        // Link up list with our adapter
        this.playerList.setAdapter(this.playerListAdapter);

        this.mHandler = this.mHandler != null ? this.mHandler : new Handler() {
            /**
             * Defines the operations to perform when this activity receives a new Message from the
             * GameController.
             *
             * @param inputMessage Message
             */
            @Override
            public void handleMessage(Message inputMessage) {
                final LobbyActivity self = LobbyActivity.this;
                switch (inputMessage.what) {
                    case GameController.UPDATE_PLAYER_LIST_NOTIFICATION:
                        self.playerListAdapter.notifyDataSetChanged();
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
        };

        // Register a handler for GameController to feedback event from GameServer
        this.gameController.registerHandler(this.mHandler);

        // Write Server IP
        String ipv4 = this.gameController.getServerIP();
        this.hostIP.setText(ipv4);

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, StopwatchActivity.class);
                Log.d(TAG, "stopwatch button clicked");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Define the media player for playing background music
        this.initializeBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Play the background music
        if (this.mp != null && !this.mp.isPlaying()) {
            this.mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Pause the background music
        this.mp.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Stop the background music
        this.mp.stop();
        this.mp.release();
        this.mp = null;
    }

    /**
     * Helper methods initialize the media player for playing background music
     */
    protected void initializeBackgroundMusic() {
        if (this.mp == null) {
            // Define the media player for playing background music
            this.mp = MediaPlayer.create(LobbyActivity.this, R.raw.mario);

            // Loop the music
            this.mp.setLooping(true);
        }
    }

    @Override
    public void onBackPressed() {
        final LobbyActivity self = LobbyActivity.this;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quit Lobby");
        mp.stop();
        dialog.setMessage(
                "Are you sure you want to quit the lobby?"
        );
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(true);
        dialog.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get a game manager reference
                        GameController gameController = GameController.getInstance();

                        // Stop the GameServer if it is running
                        gameController.stopGameServer();

                        // Let super class do rest of the stuff
                        LobbyActivity.super.onBackPressed();
                    }
                }
        );
        dialog.setNegativeButton(
                "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Replay the background music
                        self.mp.stop();
                        self.mp.release();
                        self.mp = null;

                        self.initializeBackgroundMusic();
                        self.mp.start();
                    }
                }
        );

        dialog.show();
    }
}
