package com.iems5722.partytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class LobbyActivity extends Activity {
    private static final String TAG = LobbyActivity.class.getClass().getSimpleName();

    protected Button startButton = null;
    protected TextView hostIP = null;
    protected ListView playerList = null;

    protected PlayerItemAdapter playerListAdapter = null;

    protected GameController gameController = null;

    // Handler reference
    protected Handler mHandler = null;

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
                LobbyActivity self = LobbyActivity.this;
                switch (inputMessage.what) {
                    case GameController.JOIN_HOST_RESPONSE:
                        GameController.JoinHostResponse
                                obj = (GameController.JoinHostResponse) inputMessage.obj;
                        if (obj.isSuccess) {
                            self.playerListAdapter.notifyDataSetChanged();
                        }
                        break;
                    case GameController.UPDATE_PLAYER_LIST_NOTIFICATION:
                        self.playerListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        // Register a handler for GameController to feedback event from GameServer
        this.gameController.registerHandler(this.mHandler);

        // Write Server IP
        String ipv4 = this.gameController.getServerIP();
        this.hostIP.setText(ipv4);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quit Lobby");
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
                    }
                }
        );

        dialog.show();
    }
}
