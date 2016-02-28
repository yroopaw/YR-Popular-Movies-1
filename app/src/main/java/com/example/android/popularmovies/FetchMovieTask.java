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
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.data.MovieContract.MovieListEntry;
import com.example.android.popularmovies.data.MovieContract.HighestRatedMovies;

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

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    //yy private movieInfoAdapter mMovieAdapter;
    private boolean DEBUG = true;

    public FetchMovieTask(Context context) {
        mContext = context;
        //yy  mMovieAdapter = movieAdapter;
    }


    private void getMovieDataFromJson(String movieJsonStr, String sortType)
            throws JSONException {

        //final String LOG_TAG = getMovieDataFromJson.class.getSimpleName();

        final String LOG_TAG = "getMovieDataFromJson";

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_POPULARITY = "popularity";
        final String MDB_ORIGINAL_TITLE = "original_title";
        final String MDB_POSTER_PATH_THUMBNAIL = "poster_path";
        final String MDB_PLOT_SYNOPSIS = "overview";
        final String MDB_USER_RATING = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_ID = "id";


        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);


            // Insert the new  movie information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject singleMovie = movieArray.getJSONObject(i);


                ContentValues movieValues = new ContentValues();

                String yy = mContext.getString(R.string.pref_sort_favourite);

                if (sortType.equals(mContext.getString(R.string.pref_sort_popular))) {
                    Log.v(LOG_TAG, "Sort: Order Popular");

                    movieValues.put(MovieListEntry.COLUMN_MOVIE_ID, singleMovie.getString(MDB_ID));
                    movieValues.put(MovieListEntry.COLUMN_POPULARITY, singleMovie.getInt(MDB_POPULARITY));
                    movieValues.put(MovieListEntry.COLUMN_ORIGINAL_TITLE, singleMovie.getString(MDB_ORIGINAL_TITLE));
                    movieValues.put(MovieListEntry.COLUMN_PLOT_SYNOPSIS, singleMovie.getString(MDB_PLOT_SYNOPSIS));
                    movieValues.put(MovieListEntry.COLUMN_USER_RATING, singleMovie.getDouble(MDB_USER_RATING));
                    movieValues.put(MovieListEntry.COLUMN_RELEASE_DATE, singleMovie.getString(MDB_RELEASE_DATE));
                    movieValues.put(MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL, singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL));

                } else if (sortType.equals(mContext.getString(R.string.pref_sort_highestrated))) {
                    Log.v(LOG_TAG, "Sort: Order Highest Rated");
                    movieValues.put(HighestRatedMovies.COLUMN_MOVIE_ID, singleMovie.getString(MDB_ID));
                    movieValues.put(HighestRatedMovies.COLUMN_POPULARITY, singleMovie.getInt(MDB_POPULARITY));
                    movieValues.put(HighestRatedMovies.COLUMN_ORIGINAL_TITLE, singleMovie.getString(MDB_ORIGINAL_TITLE));
                    movieValues.put(HighestRatedMovies.COLUMN_PLOT_SYNOPSIS, singleMovie.getString(MDB_PLOT_SYNOPSIS));
                    movieValues.put(HighestRatedMovies.COLUMN_USER_RATING, singleMovie.getDouble(MDB_USER_RATING));
                    movieValues.put(HighestRatedMovies.COLUMN_RELEASE_DATE, singleMovie.getString(MDB_RELEASE_DATE));
                    movieValues.put(HighestRatedMovies.COLUMN_POSTER_PATH_THUMBNAIL, singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL));

                } else if (sortType.equals(mContext.getString(R.string.pref_sort_favourite))) {
                    Log.v(LOG_TAG, "Sort: Order Favorite");
                    movieValues.put(MovieListEntry.COLUMN_MOVIE_ID, singleMovie.getString(MDB_ID));
                    movieValues.put(MovieListEntry.COLUMN_POPULARITY, singleMovie.getInt(MDB_POPULARITY));
                    movieValues.put(MovieListEntry.COLUMN_ORIGINAL_TITLE, singleMovie.getString(MDB_ORIGINAL_TITLE));
                    movieValues.put(MovieListEntry.COLUMN_PLOT_SYNOPSIS, singleMovie.getString(MDB_PLOT_SYNOPSIS));
                    movieValues.put(MovieListEntry.COLUMN_USER_RATING, singleMovie.getDouble(MDB_USER_RATING));
                    movieValues.put(MovieListEntry.COLUMN_RELEASE_DATE, singleMovie.getString(MDB_RELEASE_DATE));
                    movieValues.put(MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL, singleMovie.getString(MDB_POSTER_PATH_THUMBNAIL));

                    //Default oto Highest Rated Movie view if sort order preference is favorite and no movie is marked as favorite
                    // Display Message to user


                } else {
                    Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
                }


                cVVector.add(movieValues);
            }
            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                if (sortType.equals(mContext.getString(R.string.pref_sort_popular))) {
                    inserted = mContext.getContentResolver().bulkInsert(MovieListEntry.CONTENT_URI, cvArray);
                } else if (sortType.equals(mContext.getString(R.string.pref_sort_highestrated))) {
                    inserted = mContext.getContentResolver().bulkInsert(HighestRatedMovies.CONTENT_URI, cvArray);
                }
            }

            // Log.d(LOG_TAG, "FetchMovieTask Complete. " + cVVector.size() + " Inserted");
            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }



    @Override
    protected Void doInBackground(String... params) {


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
                //   rowsDeleted = delete(
                //     MovieContract.MovieListEntry.TABLE_NAME, null, null);
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
            getMovieDataFromJson(movieJsonStr, sortType);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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

        return null;
    }

  /*  @Override
    protected void onPostExecute(movieInfo[] result) {
        if (result != null && mMovieAdapter != null) {
            mMovieAdapter.clear();
            for (movieInfo singleMovie : result) {
                mMovieAdapter.add(singleMovie);
            }
            // New data is back from the server.  Hooray!
        }
    }*/
}