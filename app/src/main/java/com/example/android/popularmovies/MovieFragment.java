package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.popularmovies.R.id.fill_horizontal;
import static com.example.android.popularmovies.R.id.fill_vertical;
import static com.example.android.popularmovies.R.id.gridview_movies;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private movieInfoAdapter mMovieAdapter;

    public MovieFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mMovieAdapter = new movieInfoAdapter(getActivity(), new ArrayList<movieInfo>());

        //Get reference to Gridview and set adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        gridView.setAdapter(mMovieAdapter);

        //For getting Detail View
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                movieInfo clickedMovie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movieDetail", clickedMovie);

                startActivity(intent);
            }
        });



        return rootView;
    }

    @Override
    public  void onStart() {
        super.onStart();
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    private movieInfo[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        //final String LOG_TAG = getMovieDataFromJson.class.getSimpleName();

        final String LOG_TAG = "getMovieDataFromJson";

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_ORIGINAL_TITLE = "original_title";
        final String MDB_POSTER_PATH_THUMBNAIL = "poster_path";
        final String MDB_PLOT_SYNOPSIS = "overview";
        final String MDB_USER_RATING = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

        movieInfo[] resultStrs = new movieInfo[movieArray.length()];

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject singleMovie = movieArray.getJSONObject(i);

            resultStrs[i] =  new movieInfo (i,
                                            singleMovie.getString(MDB_ORIGINAL_TITLE),
                                            singleMovie.getString(MDB_PLOT_SYNOPSIS),
                                            singleMovie.getDouble(MDB_USER_RATING),
                                            singleMovie.getString(MDB_RELEASE_DATE),
                                            singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL));
             }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));


        if (sortType.equals(getString(R.string.pref_sort_popular)))
        {
            Log.v(LOG_TAG, "Sort: Order Popular");
            resultStrs = sortByPopularity(resultStrs);
        } else if  (sortType.equals(getString(R.string.pref_sort_highestrated))) {
            Log.v(LOG_TAG, "Sort: Order Highest Rated");
            resultStrs = sortByHighestRated(resultStrs);
        } else {
            Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
        }

        /*for (movieInfo s : resultStrs) {
            Log.v(LOG_TAG, "Movie entry: " + s);
        }*/
        return resultStrs;

    }

    public static movieInfo[] sortByHighestRated(movieInfo[] movies) {
        for (int i = 1; i < movies.length; i++) {
            for (int j = 0; j < movies.length - i; j++) {
                if (((movies[j].user_rating).compareTo((movies[j+1].user_rating))) < 0) {

                    movieInfo temp = movies[j];
                    movies[j] = movies[j + 1];
                    movies[j + 1] = temp;
                }
            }
        }
        return movies;
    }

    public static movieInfo[] sortByPopularity(movieInfo[] movies) {
        for (int i = 1; i < movies.length; i++) {
            for (int j = 0; j < movies.length - i; j++) {
                if ((movies[j].popularity) >((movies[j+1].popularity)))  {

                    movieInfo temp = movies[j];
                    movies[j] = movies[j + 1];
                    movies[j + 1] = temp;
                }
            }
        }
        return movies;
    }


    // To display:
    // original title -> original_title
    // movie poster image thumbnail -> poster_path
    // A plot synopsis (called overview in the api)
    //  user rating (called vote_average in the api)
    //  release date ->release_date

    public class FetchMovieTask extends AsyncTask<String, Void, movieInfo[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected movieInfo[] doInBackground(String... params) {

            // These two need to be declared outside the try / catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.

            String movieJsonStr = null;
            String sortFormat = "popularity.desc";
            String apiId = getString(R.string.api_key);

            try {

                // Construct the URL for the moviedb query


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_ID_PARAM = "api_key";


               /* SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortType = pref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));


                if (sortType.equals(getString(R.string.pref_sort_popular)))
                {
                    Log.v(LOG_TAG, "Sort: Order Popular");
                    sortFormat = "popularity.desc";
                } else if  (sortType.equals(getString(R.string.pref_sort_highestrated))) {
                    Log.v(LOG_TAG, "Sort: Order Highest Rated");
                    sortFormat = "vote_average.desc";
                } else {
                    Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
                }*/

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortFormat)
                        .appendQueryParameter(API_ID_PARAM, apiId)
                        .build();


                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {

                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

         try {
              return getMovieDataFromJson(movieJsonStr);


           } catch (JSONException e) {

             Log.e(LOG_TAG, e.getMessage(), e);
             e.printStackTrace();
             }

            return null;

        }

        @Override
        protected void onPostExecute(movieInfo[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                for (movieInfo singleMovie : result) {
                       mMovieAdapter.add(singleMovie);
                }
            }
        }
    }
}




