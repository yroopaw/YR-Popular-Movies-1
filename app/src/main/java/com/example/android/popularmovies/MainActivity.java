package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private static final String DETAILACTIVTYFRAGMENT_TAG = "DFTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    // private  static final String MOVIEFRAGMENT_TAG = "MFTAG";
    private boolean mTwoPane;
    private String mSortOrder;

    public static String getPreferredSortOrder(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String sortType = pref.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));

        return sortType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortOrder = getPreferredSortOrder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri contentUri;
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts(res/layout-sw600dp).
            //  If this view is present, then the activity should be in two-pane mode.

            mTwoPane = true;
            editor.putBoolean("mTwoPane_key", true);
            editor.commit();

            // In two-pane mode, show the detail view in this activity by adding or
            // replacing the detail fragment using a fragment transaction.

            if (savedInstanceState == null) {

                //Added to display first record or grid view in detail iew for two pane
                Bundle args = new Bundle();
                // Uri BASE_CONTENT_URI = MovieContract.MovieListEntry.buildMovieListUri();

                //   contentUri =
                //     BASE_CONTENT_URI.buildUpon().appendPath("135397").build();

                contentUri = Utility.getUriOfFirstMovieInPreferredSortOrder(this);
                args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

                DetailActivityFragment fragment = new DetailActivityFragment();
                fragment.setArguments(args);



                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILACTIVTYFRAGMENT_TAG)
                        .commit();

            } else {
                mTwoPane = false;
                editor.putBoolean("mTwoPane_key", false);
                editor.commit();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu); //explode detail_menu
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

    @Override
    protected void onResume() {

        super.onResume();
        String sortOrder = getPreferredSortOrder(this);


        if (sortOrder.equals(this.getString(R.string.pref_sort_favourite))) {
            Uri movieListFromDBQueryUri = MovieContract.FavoriteMovies.buildMovieListUri();

            Cursor cur = this.getContentResolver().query(movieListFromDBQueryUri,
                    null, null, null, null);

            if (cur != null && cur.getCount() == 0) {
                Log.v(LOG_TAG, "Converted favoRite to mostPopular as favoRite is empty");
                sortOrder = this.getString(R.string.pref_sort_popular);
            }
        }

        //update the movie list in our second pane using the fragment manager

        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            Log.v(LOG_TAG, "On Resume - Sort order" + sortOrder + mSortOrder);
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if (null != mf) {
                Log.v(LOG_TAG, "XYZ On Resume - Entered mf not null");
                mf.onSortOrderChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILACTIVTYFRAGMENT_TAG);
            if (null != df) {
                Bundle args = new Bundle();

                Uri contentUri = Utility.getUriOfFirstMovieInPreferredSortOrder(this);
                args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);
                DetailActivityFragment fragment = new DetailActivityFragment();
                fragment.setArguments(args);

                Log.v(LOG_TAG, "XYZ On Resume - Entered df not null");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILACTIVTYFRAGMENT_TAG)
                        .commit();
                //  df.onSortOrderChanged();
                // df.onSortOrderChanged(sortOrder);

            }
            mSortOrder = sortOrder;

        }
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by adding
            //  or replacing the detail fragment using a fragment transaction.

            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILACTIVTYFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


}
