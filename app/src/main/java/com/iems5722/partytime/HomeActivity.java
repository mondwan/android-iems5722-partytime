package com.iems5722.partytime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class HomeActivity extends PortraitOnlyActivity {
    private static final String TAG = HomeActivity.class.getClass().getSimpleName();

    protected WifiManager wm;

    protected ConnectivityManager cm;

    protected class NoWifiException extends Exception {
    }

    protected class SetupGameServerException extends Exception {
    }

    // Magic number define the wifi ap status
    // private final static int WIFI_AP_DISABLED = 1;
    private final static int WIFI_AP_ENABLED = 3;

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
        Button crazyClickButton = (Button) this.findViewById(R.id.crazyClickButton);
        Button colorResponseButton = (Button) this.findViewById(R.id.colorResponse);
        Button patternButton = (Button) this.findViewById(R.id.patternButton);
        Button queueButton = (Button) this.findViewById(R.id.queueButton);

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

        crazyClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CrazyClickActivity.class);
                Log.d(TAG, "crazy button clicked");
                startActivity(intent);
            }
        });

        colorResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ColorResponseActivity.class);
                Log.d(TAG, "crazy button clicked");
                startActivity(intent);
            }
        });

        patternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PatternActivity.class);
                Log.d(TAG, "pattern button clicked");
                startActivity(intent);
            }
        });

        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GameSequenceActivity.class);
                Log.d(TAG, "queue button clicked");
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

        try {
            // We are not able to get IP via wireless manager
            if (ret.equals("0.0.0.0")) {
                Enumeration<NetworkInterface> en;
                Enumeration<InetAddress> enumIpAddr;
                for (en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    // TODO: Name of virtual interfaces varied over devices
                    if (intf.getName().contains("wlan")) {
                        for (enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()
                                    && (inetAddress.getAddress().length == 4)) {
                                Log.d(TAG, inetAddress.getHostAddress());
                                ret = inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        }

        return ret;
    }

    /**
     * Helper method alert players to enable wifi if they have not done yet
     */
    protected boolean checkWifiAvailability() {
        NetworkInfo networkInfo = this.cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean ret =
                this.getWifiApState() ||
                        (this.wm.isWifiEnabled() && networkInfo.isConnected());

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
            dialog.setNeutralButton("I am hotspot", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeActivity self = HomeActivity.this;

                    boolean ret = self.getWifiApState();

                    if (!ret) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
            });
            dialog.show();
        }

        return ret;
    }

    protected boolean getWifiApState() {
        boolean ret;
        try {
            Method method = this.wm.getClass().getMethod("getWifiApState");

            int tmp = ((Integer)method.invoke(this.wm));

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            Log.d(TAG, String.format("tmp = |%d|", tmp));

            ret = tmp == WIFI_AP_ENABLED;
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            ret = false;
        }

        return ret;
    }
}
