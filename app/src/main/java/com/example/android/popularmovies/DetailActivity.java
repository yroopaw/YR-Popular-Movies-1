package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //setContentView(R.layout.content_detail); // activity_detail


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
           // arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

         //   DetailActivityFragment fragment = new DetailActivityFragment();

            getSupportFragmentManager().beginTransaction().
                    add(R.id.movie_detail_container, new DetailActivityFragment())
                    .commit();


         //   fragment.setArguments(arguments);

       //     getSupportFragmentManager().beginTransaction()
        //            .add(R.id.weather_detail_container, fragment)
          //          .commit();
        }
      //yy  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //This is not avaibale in activity_detail
        //yy setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu); //explode detail_menu

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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
