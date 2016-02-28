package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // These indices are tied to MOVIE_LIST_COLUMNS.  If MOVIE_LIST_COLUMNS changes, these must change.
    static final int COL_INDEX_ID = 0; // this for default column _ID
    static final int COL_MOVIE_ID = 1;
    static final int COL_POPULARITY = 2;
    static final int COL_ORIGINAL_TITLE = 3;
    static final int COL_PLOT_SYNOPSIS = 4;
    static final int COL_USER_RATING = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_POSTER_PATH_THUMBNAIL = 7;
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    //For Projections
    private static final String[] MOVIE_LIST_COLUMNS = {
            MovieContract.MovieListEntry._ID,
            MovieContract.MovieListEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieListEntry.COLUMN_POPULARITY,
            MovieContract.MovieListEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieListEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieListEntry.COLUMN_USER_RATING,
            MovieContract.MovieListEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL

    };
    private static final int MOST_POPULAR = 1;
    private static final int HIGHEST_RATED = 2;
    private static final int FAVORITE = 3;
    private static final int SORT_ERROR = 100;
    private MovieAdapter mMovieAdapter;


    public MovieFragment() {
    }

    public static Uri getUriBasedOnPreferredSortOrder(Context context) {

        Uri sortOrderDbUri = MovieContract.MovieListEntry.buildMovieListUri();
        ;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String sortType = pref.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_popular));

        if (sortType.equals(context.getString(R.string.pref_sort_popular))) {

            sortOrderDbUri = MovieContract.MovieListEntry.buildMovieListUri();

        } else if (sortType.equals(context.getString(R.string.pref_sort_highestrated))) {

            sortOrderDbUri = MovieContract.HighestRatedMovies.buildMovieListUri();

        } else if (sortType.equals(context.getString(R.string.pref_sort_favourite))) {


            sortOrderDbUri = MovieContract.FavoriteMovies.buildMovieListUri();

            Cursor cur = context.getContentResolver().query(sortOrderDbUri,
                    null, null, null, null);

            if (cur != null && cur.getCount() == 0) {
                Log.v(LOG_TAG, "Hard code sort order if favorite is empty");
                sortOrderDbUri = MovieContract.MovieListEntry.buildMovieListUri();
            }


            //original    sortOrderDbUri = MovieContract.FavoriteMovies.buildMovieListUri();

        } else {
            Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
        }


        return sortOrderDbUri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar
        int id = item.getItemId();

      /*  if (id == R.id.action_refresh) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute();
            return true;
        }*/

        return super.onOptionsItemSelected(item);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Uri movieListFromDBQueryUri = MovieContract.MovieListEntry.buildMovieListUri();

        Uri movieListFromDBQueryUri = getUriBasedOnPreferredSortOrder(getActivity());

        // final Cursor cur = getActivity().getContentResolver().query(movieListFromDBQueryUri, //query(Uri uri,
        //         null, null, null, sortOrder); //String[] projection, String selection, String[] selectionArgs,String sortOrder)

        String searchString = "10";
        String[] selectionArgs = new String[]{searchString};
        String selectionClause = MovieContract.MovieListEntry.COLUMN_USER_RATING + " =?";

        //final Cursor cur = getActivity().getContentResolver().query(movieListFromDBQueryUri, //query(Uri uri,
        //   null, selectionClause, selectionArgs, sortOrder); //String[] projection, String selection, String[] selectionArgs,String sortOrder)

        final Cursor cur = getActivity().getContentResolver().query(movieListFromDBQueryUri, //query(Uri uri,
                null, null, null, null);

        // The CursorAdapter will take data from our cursor and populate the GridView.
        mMovieAdapter = new MovieAdapter(getActivity(), cur, 0);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get reference to Gridview and set adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        gridView.setAdapter(mMovieAdapter);

        //We will call our main activity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String movieClicked = cursor.getString(COL_MOVIE_ID);
                if (cursor != null) {
                    //String locationSetting = Utility.getPreferredLocation(getActivity());
                 /*   ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieListEntry.selectedMovieQuery(movieClicked)
                            ));*/

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sortType = pref.getString(getActivity().getString(R.string.pref_sort_key), getActivity().getString(R.string.pref_sort_popular));

                    if (sortType.equals(getActivity().getString(R.string.pref_sort_popular))) {
                        Log.v("ABC", "ABC - mostPopular" + MovieContract.MovieListEntry.selectedMovieQuery(cursor.getString(COL_MOVIE_ID)));

                        ((Callback) getActivity())
                                .onItemSelected(MovieContract.MovieListEntry.selectedMovieQuery(
                                        cursor.getString(COL_MOVIE_ID)
                                ));


                    } else if (sortType.equals(getActivity().getString(R.string.pref_sort_highestrated))) {
                        Log.v("ABC", "ABC - mostPopular" + MovieContract.HighestRatedMovies.selectedMovieQuery(cursor.getString(COL_MOVIE_ID)));

                        ((Callback) getActivity())
                                .onItemSelected(MovieContract.HighestRatedMovies.selectedMovieQuery(
                                        cursor.getString(COL_MOVIE_ID)
                                ));

                    } else if (sortType.equals(getActivity().getString(R.string.pref_sort_favourite))) {

                        //***

                        Uri movieListFromDBQueryUri = MovieContract.FavoriteMovies.buildMovieListUri();

                        Cursor cur = getActivity().getContentResolver().query(movieListFromDBQueryUri,
                                null, null, null, null);

                        if (cur != null && cur.getCount() == 0) {
                            Log.v(LOG_TAG, "Converted favoRite to mostPopular as favoRite is empty");
                            ((Callback) getActivity())
                                    .onItemSelected(MovieContract.MovieListEntry.selectedMovieQuery(
                                            cursor.getString(COL_MOVIE_ID)
                                    ));
                        } else {
                            Log.v("ABC", "ABC - favorite" + MovieContract.FavoriteMovies.selectedMovieQuery(cursor.getString(COL_MOVIE_ID)));
                            //***
                            ((Callback) getActivity())
                                    .onItemSelected(MovieContract.FavoriteMovies.selectedMovieQuery(
                                            cursor.getString(COL_MOVIE_ID)
                                    ));
                        }

                    } else {
                        Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
                    }


                }
            }
        });

        //For getting Detail View
       /* gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                movieInfo clickedMovie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movieDetail", clickedMovie);

                startActivity(intent);
            }
        });*/

        /*
        @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                }

                 DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                 @Poornima
         */

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortOrderChanged() {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String sortOrder = MainActivity.getPreferredSortOrder(getActivity());
        movieTask.execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Uri movieListFromDBQueryUri = MovieContract.MovieListEntry.buildMovieListUri();

        Uri movieListFromDBQueryUri = getUriBasedOnPreferredSortOrder(getActivity());

        Cursor cur = getActivity().getContentResolver().query(movieListFromDBQueryUri,
                null, null, null, null);

        int yy = cur.getCount();

        if (cur != null && cur.getCount() == 0) {
            updateMovies();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //  Uri movieListUri = MovieContract.MovieListEntry.buildMovieListUri();
        Uri movieListUri = getUriBasedOnPreferredSortOrder(getActivity());

     /*   Cursor cur = getActivity().getContentResolver().query(movieListUri,
                null, null, null, null);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = pref.getString(context.getString(R.string.pref_sort_key), getActivity().getString(R.string.pref_sort_popular),getActivity().getString(R.string.pref_sort_favourite));

        if (cur != null && cur.getCount() == 0) {
            updateMovies();
        }
*/
        return new CursorLoader(getActivity(),
                movieListUri,
                MOVIE_LIST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    public interface Callback {
        /**
         * Movie FragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri selectedMovieUri);
    }




}




