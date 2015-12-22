package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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

        if (id == R.id.action_refresh) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //yy   mMovieAdapter  =
        // yy       new ArrayAdapter<String>(
        //yy          getActivity(), // The Currrent Context (this activity)
        //yy      R.layout.list_item_movie, // The name of Layout id
        //yy  R.id.list_item_forecast_imageview, // The id of ImageView to populate
        //yy new ArrayList<String>());

        movieInfo[] movieList = {
                new movieInfo( "A", "B", 1.1, "C" , "http://i.imgur.com/BewnAhMb.jpg"),
                new movieInfo( "D", "E", 2.1, "F" , "http://i.imgur.com/k6OTHWwb.jpg"),
                new movieInfo( "H", "I", 3.1, "J" , "http://i.imgur.com/ZLQX8Jbb.jpg"),
                new movieInfo( "L", "M", 4.1, "N" , "http://i.imgur.com/C8M1iSZb.jpg"),
                new movieInfo( "P", "Q", 5.1, "R" , "http://i.imgur.com/BewnAhMb.jpg"),
                new movieInfo( "T", "U", 6.1, "V" , "http://i.imgur.com/k6OTHWwb.jpg"),
                new movieInfo( "Y", "Z", 7.1, "A" , "http://i.imgur.com/ZLQX8Jbb.jpg"),
                new movieInfo( "C", "D", 8.1, "E" , "http://i.imgur.com/C8M1iSZb.jpg")

        };

        mMovieAdapter = new movieInfoAdapter(getActivity(), Arrays.asList(movieList));


        //Get reference to Gridview and set adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
       // mMovieAdapter =  movieInfoAdapter(getContext());
        gridView.setAdapter(mMovieAdapter);



        return rootView;
    }

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
            
            String apiId = "Not shared";

            try {

                // Construct the URL for the moviedb query


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_ID_PARAM = "api_key";

                //  http://api.themoviedb.org/3/discover/movie? sort_by=popularity.desc&api_key=1aa14e3bc0a68048c86623e99ebc2240


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortFormat)
                        .appendQueryParameter(API_ID_PARAM, apiId)
                        .build();


                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
               //nn mMovieAdapter.addAll(result);
             //   mMovieAdapter = new movieInfoAdapter(getActivity(), Arrays.asList(result));




                for (movieInfo singleMovie : result) {
                    //for (String dayForecastStr : result) {
                       mMovieAdapter.add(singleMovie);
                        //   mForecastAdapter.add(dayForecastStr);
                    }
                }
            }
        }


    // To display:
    // original title -> original_title
    // movie poster image thumbnail -> poster_path
    // A plot synopsis (called overview in the api)
    //  user rating (called vote_average in the api)
    //  release date ->release_date

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
        String arrayDelimeter = "#";

        for(int i = 0; i < movieArray.length(); i++) {
            // Use the following format of results data sorted by Popularity
            //Delimeter is #, define in variable arrayDelimeter
            // original_title#poster_path#overview#vote_average#release_date


            // Get the JSON object representing the day
            JSONObject singleMovie = movieArray.getJSONObject(i);



           // JSONObject originalTitleObject = singleMovie.getJSONObject(MDB_ORIGINAL_TITLE);
           // String original_title = singleMovie.getString(MDB_ORIGINAL_TITLE);
           // String poster_path = singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL);


           // resultStrs[i] = singleMovie.getString(MDB_ORIGINAL_TITLE) + arrayDelimeter +
             //               singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL) + arrayDelimeter +
               //             singleMovie.getString(MDB_PLOT_SYNOPSIS) + arrayDelimeter +
                 //           singleMovie.getDouble(MDB_USER_RATING) + arrayDelimeter +
                   //         singleMovie.getString(MDB_RELEASE_DATE);


            resultStrs[i] =  new movieInfo (    singleMovie.getString(MDB_ORIGINAL_TITLE),
                                                singleMovie.getString(MDB_PLOT_SYNOPSIS),
                                                singleMovie.getDouble(MDB_USER_RATING),
                                                singleMovie.getString(MDB_RELEASE_DATE),
                                                singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL));

            // resultStrs[i] = day + " - " + description + " - " + highAndLow;
            //resultStrs[i] = original_title + arrayDelimeter + poster_path + arrayDelimeter + overview + arrayDelimeter + vote_average + arrayDelimeter + release_date;
        }

        for (movieInfo s : resultStrs) {
            Log.v(LOG_TAG, "Movie entry: " + s);
        }
        return resultStrs;

    }
}




