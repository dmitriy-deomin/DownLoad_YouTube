package dmitriy.deomin.download_youtube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Popmenu extends Activity{

    boolean fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popmenu);

    }

    public void abaut(View view) {
        Intent i = new Intent(getApplicationContext(), Abaut_main.class);
        startActivity(i);
    }

    public void finisf(View view) {
        Intent answerInent = new Intent();
        setResult(RESULT_OK, answerInent);
        finish();
    }

    public void open_history(View view) {
        Intent h = new Intent(getApplicationContext(), History.class);
        startActivity(h);
    }
}
