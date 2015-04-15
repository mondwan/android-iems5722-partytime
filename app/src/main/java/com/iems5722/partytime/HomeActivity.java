package com.iems5722.partytime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get references for the buttons
        Button hostButton = (Button) this.findViewById(R.id.hostButton);
        Button joinButton = (Button) this.findViewById(R.id.joinButton);

        // Setup onclick listener for those buttons
        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LobbyActivity.class);
                Log.d(TAG, "host button clicked");
                startActivity(intent);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: jump to join activity
                Log.d(TAG, "join button clicked");
            }
        });
    }
}
