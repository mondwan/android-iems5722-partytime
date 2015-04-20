package com.iems5722.partytime;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kevin on 21/4/15.
 */
public class SendScoresActivity extends PortraitOnlyActivity {
    Button sendButton;
    protected GameController gameController = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        sendButton = (Button) this.findViewById(R.id.sendscores);
        this.gameController = GameController.getInstance();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int myInteger = 10;
                gameController.sendMsg(myInteger);
            }
        });
    }
}
