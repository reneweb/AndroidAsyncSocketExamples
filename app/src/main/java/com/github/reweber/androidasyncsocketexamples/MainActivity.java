package com.github.reweber.androidasyncsocketexamples;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //TCP client and server (Client will automatically send welcome message after setup and server will respond)
                new com.github.reweber.androidasyncsocketexamples.tcp.Server("localhost", 7000);
                new com.github.reweber.androidasyncsocketexamples.tcp.Client("localhost", 7000);

                //UDP client and server (Here the client explicitly sends a message)
                new com.github.reweber.androidasyncsocketexamples.udp.Server("localhost", 7001);
                new com.github.reweber.androidasyncsocketexamples.udp.Client("localhost", 7001).send("Hello World");
                return null;
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
