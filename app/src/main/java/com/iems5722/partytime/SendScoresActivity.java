package com.iems5722.partytime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kevin on 21/4/15.
 */
public class SendScoresActivity extends PortraitOnlyActivity {
    Button sendButton;
    protected GameController gameController = null;
    private static final String TAG = SendScoresActivity.class.getClass().getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        sendButton = (Button) this.findViewById(R.id.sendscores);
        this.gameController = GameController.getInstance();
        final GameController.UpdateScoresRequest req = new GameController.UpdateScoresRequest();
        final GameController.UpdateScoresResponse res = new GameController.UpdateScoresResponse();




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int myInteger = 10;


                if (gameController.isHost()){
                    res.scores = myInteger;
                    gameController.sendMsg(res);
                }
                else{
                    req.scores = myInteger;
                    gameController.sendMsg(req);
                }

                Log.d(TAG, "Send Button clicked");
            }
        });
    }
}
