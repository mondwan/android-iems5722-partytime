package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JoinHostActivity extends Activity {
    private static final String TAG = JoinHostActivity.class.getClass().getSimpleName();

    // View references
    protected TextView statusText = null;
    protected EditText hostIPInputBox = null;
    protected Button joinHostButton = null;

    // GameController
    protected GameController gameController = null;

    // Handler reference
    protected Handler mHandler = null;

    protected class InvalidIPV4Exception extends Exception {
    }

    protected class FailToConnectToGameServerException extends Exception {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_host);

        // Setup properties
        this.statusText = (TextView) this.findViewById(R.id.statusText);
        this.hostIPInputBox = (EditText) this.findViewById(R.id.hostIP);
        this.joinHostButton = (Button) this.findViewById(R.id.joinHostButton);
        this.gameController = GameController.getInstance();

        final JoinHostActivity self = JoinHostActivity.this;

        // Handle enter key
        this.hostIPInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    self.connectToGameServer(v.getText().toString());
                }

                // Pass to default Listener which is close the soft input
                return false;
            }

        });

        // Attach onClickListener
        this.joinHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipv4 = self.hostIPInputBox.getText().toString();
                self.connectToGameServer(ipv4);
            }
        });

        // Define Handler
        if (this.mHandler == null) {
            this.mHandler = new Handler() {
                /**
                 * Defines the operations to perform when this activity receives a new Message from the
                 * GameController.
                 *
                 * @param inputMessage Message
                 */
                @Override
                public void handleMessage(Message inputMessage) {
                    try {
                        switch (inputMessage.what) {
                            case GameController.CONNECT_GAMESERVER_SUCCESS:
                                // NO OPT YET
                            default:
                                throw new FailToConnectToGameServerException();
                        }
                    } catch (FailToConnectToGameServerException e) {
                        self.statusText.setText(R.string.status_connect_failure);
                        self.hostIPInputBox.setEnabled(true);
                        self.joinHostButton.setEnabled(true);
                    }
                }
            };
        }
    }

    /**
     * Connect to the game server.
     *
     * @param ipv4 String
     */

    protected void connectToGameServer(String ipv4) {
        Log.d(TAG, "Trying to connect to " + ipv4);

        this.hostIPInputBox.setEnabled(false);
        this.joinHostButton.setEnabled(false);
        this.statusText.setText(R.string.status_connecting);

        try {
            String ipv4Pattern =
                    "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(ipv4Pattern);
            Matcher matcher = pattern.matcher(ipv4);
            if (!matcher.matches()) {
                throw new InvalidIPV4Exception();
            }

            // Async call
            this.gameController.connectToGameServer(ipv4, this.mHandler);
        } catch (InvalidIPV4Exception e) {
            this.statusText.setText(R.string.status_invalid_ipv4);
            this.hostIPInputBox.setEnabled(true);
            this.joinHostButton.setEnabled(true);
        }
    }

}
