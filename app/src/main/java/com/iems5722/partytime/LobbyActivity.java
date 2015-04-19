package com.iems5722.partytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class LobbyActivity extends Activity {
    private static final String TAG = LobbyActivity.class.getClass().getSimpleName();

    protected Button startButton = null;
    protected TextView hostIP = null;
    protected ListView playerList = null;

    protected GameController gameController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Setup reference for our properties
        this.startButton = (Button) this.findViewById(R.id.startButton);
        this.hostIP = (TextView) this.findViewById(R.id.hostIP);
        this.playerList = (ListView) this.findViewById(R.id.playerList);

        this.gameController = GameController.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
