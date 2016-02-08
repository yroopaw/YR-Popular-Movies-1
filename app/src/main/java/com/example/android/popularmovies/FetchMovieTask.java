package com.example.android.popularmovies;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;



        import android.content.ContentValues;
        import android.content.Context;
        import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.preference.PreferenceManager;
        import android.text.format.Time;
        import android.util.Log;
        import android.widget.ArrayAdapter;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieListEntry;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, movieInfo[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private movieInfoAdapter mMovieAdapter;
    private final Context mContext;

    public FetchMovieTask(Context context, movieInfoAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

    private boolean DEBUG = true;



    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     * @return the row ID of the added location.
     */
   /* long addLocation(String locationSetting, String cityName, double lat, double lon) {
        // Students: First, check if the location with this city name exists in the db
        // If it exists, return the current ID
        // Otherwise, insert it using the content resolver and the base URI
        return -1;
    }*/

    /*
        Students: This code will allow the FetchMovieTask to continue to return the strings that
        the UX expects so that we can continue to test the application even once we begin using
        the database.
     */
    /*String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = formatHighLows(
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP),
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP));
            resultStrs[i] = getReadableDateString(
                    weatherValues.getAsLong(WeatherEntry.COLUMN_DATE)) +
                    " - " + weatherValues.getAsString(WeatherEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
    }*/

    /**
     * Take the String representing the complete moive list in JSON Format and
     * pull out the data we need to construct the adapter needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     *
     *
     */


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
        final String MDB_ID ="id";

        try {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);

        movieInfo[] resultStrs = new movieInfo[movieArray.length()];

        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for(int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject singleMovie = movieArray.getJSONObject(i);

            resultStrs[i] =  new movieInfo (i,
                    singleMovie.getString(MDB_ORIGINAL_TITLE),
                    singleMovie.getString(MDB_PLOT_SYNOPSIS),
                    singleMovie.getDouble(MDB_USER_RATING),
                    singleMovie.getString(MDB_RELEASE_DATE),
                    singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL),
                    singleMovie.getString(MDB_ID));

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieListEntry.COLUMN_MOVIE_ID, resultStrs[i].id);
            movieValues.put(MovieListEntry.COLUMN_POPULARITY, i);
            movieValues.put(MovieListEntry.COLUMN_ORIGINAL_TITLE, resultStrs[i].original_title);
            movieValues.put(MovieListEntry.COLUMN_PLOT_SYNOPSIS, resultStrs[i].plot_synopsis);
            movieValues.put(MovieListEntry.COLUMN_USER_RATING, resultStrs[i].user_rating);
            movieValues.put(MovieListEntry.COLUMN_RELEASE_DATE, resultStrs[i].release_date);
            movieValues.put(MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL, resultStrs[i].poster_path_thumbnail);


            cVVector.add(movieValues);
        }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MovieListEntry.CONTENT_URI, cvArray);
            }

            //yusuf Use this code to sort based on location prefence Sort order:  Ascending, by date.
           //yy String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
            String sortOrder = null;
            Uri movieListUri = MovieListEntry.buildMovieListUri();


            Cursor cur = mContext.getContentResolver().query(movieListUri,
                    null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + cVVector.size() + " Inserted");


            return resultStrs;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;


}






    @Override
    protected movieInfo[] doInBackground(String... params) {


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.

        String movieJsonStr = null;
        String sortFormat = "popularity.desc";
        String apiId = mContext.getString(R.string.api_key);



        try {
            // Construct the URL for the moviedb query


            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_ID_PARAM = "api_key";


            int rowsDeleted;


            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String sortType = pref.getString(mContext.getString(R.string.pref_sort_key), mContext.getString(R.string.pref_sort_popular));

            if (sortType.equals(mContext.getString(R.string.pref_sort_popular))) {
                Log.v(LOG_TAG, "Sort: Order Popular");
                sortFormat = "popularity.desc";
              //  rowsDeleted = db.delete(
                 //       MovieContract.MovieListEntry.TABLE_NAME, null, null);
              //  if (rowsDeleted != 0) {
                 //   mContext.getContentResolver().notifyChange(uri, null);
            } else if (sortType.equals(mContext.getString(R.string.pref_sort_highestrated))) {
                Log.v(LOG_TAG, "Sort: Order Highest Rated");
                sortFormat = "vote_average.desc";
           //     rowsDeleted = db.delete(
                //        MovieContract.MovieListEntry.TABLE_NAME, null, null);
              //      if (rowsDeleted != 0) {
                //        mContext.getContentResolver().notifyChange(uri, null);
            } else if (sortType.equals(mContext.getString(R.string.pref_sort_favourite))) {
                Log.v(LOG_TAG, "Sort: Order Favorite");
                //TODO routine for Favorite
            } else {
                Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
            }





         /*   if (sortType.equals(mContext.getString(R.string.pref_sort_popular))) {
                Log.v(LOG_TAG, "Sort: Order Popular");
                sortFormat = "popularity.desc";
            } else if (sortType.equals(mContext.getString(R.string.pref_sort_highestrated))) {
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

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
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
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
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
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(movieInfo[] result) {
        if (result != null && mMovieAdapter != null) {
            mMovieAdapter.clear();
            for(movieInfo singleMovie : result) {
                mMovieAdapter.add(singleMovie);
            }
            // New data is back from the server.  Hooray!
        }
    }
}