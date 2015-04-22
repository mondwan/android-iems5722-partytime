package com.iems5722.partytime;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;


public class CountDownActivity extends PortraitOnlyActivity {

    TextView cdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        cdView = (TextView) this.findViewById(R.id.cdViewer);

        // @TODO Be progress loading for game clients sync
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
//                cdView.setText("" + millisUntilFinished / 1000);
                if (((int) millisUntilFinished / 1000) == 3) {
                    cdView.setText("GET!! SET ~~");
                } else if (((int) millisUntilFinished / 1000) == 2) {
                    cdView.setText("Ready to GO??!!");
                } else if (((int) millisUntilFinished / 1000) == 1) {
                    cdView.setText("Next Game is: " + GameSequenceActivity.getButtonText());
                }
            }

            public void onFinish() {
                finish();
            }
        }.start();

    }
}
