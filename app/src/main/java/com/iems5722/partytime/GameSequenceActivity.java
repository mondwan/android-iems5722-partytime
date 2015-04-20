package com.iems5722.partytime;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameSequenceActivity extends ActionBarActivity {

    final String TAG = "GameSequence";
    final public static String SCORE_CODE = "SCORE_CODE";

    final int crazyGameRequestCode = 0;
    final int colorResponseCode = 1;

    Button testButton;
    TextView scoreView;


    private void startCrazyGame() {
        Intent intent = new Intent(GameSequenceActivity.this, CrazyClickActivity.class);
        startActivityForResult(intent, crazyGameRequestCode);
    }

    private void startColorResponseGame() {
        Intent intent = new Intent(GameSequenceActivity.this, ColorResponseActivity.class);
        startActivityForResult(intent, colorResponseCode);
    }

    private void startCD() {
        Intent intent = new Intent(GameSequenceActivity.this, CountDownActivity.class);
        startActivity(intent);
    }

    private void startAll() {
        startCrazyGame();
        startColorResponseGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sequence);

        scoreView = (TextView) this.findViewById(R.id.scoreView);

        testButton = (Button) this.findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "crazy button clicked");
                startAll();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode == crazyGameRequestCode &&
        if (resultCode == RESULT_OK && data != null) {
            int score = data.getIntExtra(SCORE_CODE, 100);
            scoreView.setText(Integer.toString(score));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_sequence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
