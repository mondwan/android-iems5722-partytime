package com.iems5722.partytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;


public class LobbyActivity extends Activity {
    private static final String TAG = LobbyActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


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
