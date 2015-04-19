package com.iems5722.partytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getClass().getSimpleName();

    protected WifiManager wm;

    protected ConnectivityManager cm;

    protected class NoWifiException extends Exception {
    }

    protected class SetupGameServerException extends Exception {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get reference for wifi manager
        this.wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        // Get reference for connectivity manager
        this.cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get references for the buttons
        Button hostButton = (Button) this.findViewById(R.id.hostButton);
        Button joinButton = (Button) this.findViewById(R.id.joinButton);
        Button stopwatchButton = (Button) this.findViewById(R.id.stopwatchButton);

        // Setup onclick listener for those buttons
        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "host button clicked");
                HomeActivity self = HomeActivity.this;

                try {
                    // Make sure WIFI is available before going to the lobby
                    if (!self.checkWifiAvailability()) {
                        throw new NoWifiException();
                    }

                    // Get IP address
                    String ip = self.getMyIP();
                    Log.d(TAG, String.format("My IP address |%s|", ip));

                    // Get a game manager reference
                    GameController gameController = GameController.getInstance();

                    // Record our local ip
                    gameController.setLocalIP(ip);

                    // Setup a game server with IP
                    boolean res = gameController.createGameServer(ip);

                    // Make sure there are no errors for setting up a server
                    if (!res) {
                        throw new SetupGameServerException();
                    }

                    // Go to Lobby activity
                    Intent intent = new Intent(HomeActivity.this, LobbyActivity.class);
                    startActivity(intent);
                } catch (NoWifiException e) {
                    // NO op
                } catch (SetupGameServerException e) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(self);
                    dialog.setTitle("Sorry");
                    dialog.setMessage(
                            "We are not able to setup game server yet. Please try again later on"
                    );
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setCancelable(true);
                    dialog.setNeutralButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }
                    );
                    dialog.show();
                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "join button clicked");
                HomeActivity self = HomeActivity.this;

                try {
                    // Make sure WIFI is available before going to the lobby
                    if (!self.checkWifiAvailability()) {
                        throw new NoWifiException();
                    }

                    // Get IP address
                    String ip = self.getMyIP();
                    Log.d(TAG, String.format("My IP address |%s|", ip));

                    // Get a game manager reference
                    GameController gameController = GameController.getInstance();

                    // Record our local ip
                    gameController.setLocalIP(ip);

                    // Go to JoinHostActivity
                    Intent intent = new Intent(HomeActivity.this, JoinHostActivity.class);
                    startActivity(intent);
                } catch (NoWifiException e) {
                    // NO op
                }
            }
        });
        stopwatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StopwatchActivity.class);
                Log.d(TAG, "stopwatch button clicked");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Get a game manager reference
        GameController gameController = GameController.getInstance();

        // Stop the GameServer if it is running
        gameController.stopGameServer();

        // Let super class do rest of the stuff
        super.onBackPressed();
    }

    /**
     * Helper method gets my IP address
     *
     * @return IPV4 in string (A.B.C.D)
     */
    protected String getMyIP() {
        int ipAddress = this.wm.getConnectionInfo().getIpAddress();
        String ret = String.format(
                "%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff)
        );

        return ret;
    }

    /**
     * Helper method alert players to enable wifi if they have not done yet
     */
    protected boolean checkWifiAvailability() {
        NetworkInfo networkInfo = this.cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean ret = this.wm.isWifiEnabled() && networkInfo.isConnected();

        if (!ret) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Remind");
            dialog.setMessage(
                    "You have not connect to any wireless network yet. Connect now?"
            );
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setCancelable(true);
            dialog.setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wm.setWifiEnabled(true);
                            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        }
                    }
            );
            dialog.setNegativeButton(
                    "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // NO op
                        }
                    }
            );
            dialog.show();
        }

        return ret;
    }
}
