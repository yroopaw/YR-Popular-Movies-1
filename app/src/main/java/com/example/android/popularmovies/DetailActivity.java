package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.widget.LinearLayout.LayoutParams;

public class DetailActivity extends ActionBarActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private trailerReviewsAdapter mtrailerReviewsAdapter;
    private GoogleApiClient client;
    private String movieId = null;
    private trailerReviews[] mHeader = new trailerReviews[1];
    private trailerReviews[] mTrailer= new trailerReviews[1]; // To remove static count  of items after Async Problem is fixed
    private trailerReviews[] mReviews= new trailerReviews[2]; // To remove static count  of items after Async Problem is fixed
    private trailerReviews[] mtrailerReviews = new trailerReviews[3]; // To remove static count  of items after Async Problem is fixed
    int DETAIL_HEADER = 0;
    int TRAILER = 1;
    int REVIEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
    public void onStart() {
        super.onStart();
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        trailerTask.execute();

        FetchReviewTask reviewTask = new FetchReviewTask();
       reviewTask.execute();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Log.v("OnStart", "OnStart: " + mtrailerReviews);

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.popularmovies/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.android.popularmovies/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        //removed static from this class

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for movie data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movieDetail")) {


                movieInfo clickedMovie = intent.getParcelableExtra("movieDetail");

                //Build URl for the Poster Thumbnail

                final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
                String posterUrl = POSTER_BASE_URL + this.getString(R.string.poster_resolution)
                        + "/" + clickedMovie.poster_path_thumbnail;


                //Populate mtrailerReviews with header row of movie details
                //               if(mHeader != null) {
                //                  for (int i = 0; i < mHeader.length; i++) {
                //                     mHeader[i] = null;
                //                 }
                //            }
              /*  mHeader[0].type = DETAIL_HEADER;
                mHeader[0].id = clickedMovie.release_date.substring(0, 4);
                mHeader[0].url = posterUrl;
                mHeader[0].name_Author = clickedMovie.original_title;*/

                mHeader[0] =   new trailerReviews (
                        DETAIL_HEADER,
                        clickedMovie.release_date.substring(0, 4),
                        posterUrl,
                        clickedMovie.original_title);


                ((TextView) rootView.findViewById(R.id.v_original_title))
                        .setText(clickedMovie.original_title);


                ImageView movieThumbnail = (ImageView) rootView.findViewById(R.id.v_poster_thumbnail);
                Picasso.with(getActivity()).load(posterUrl).into(movieThumbnail);





                ((TextView) rootView.findViewById(R.id.v_release_date))
                        .setText(clickedMovie.release_date.substring(0, 4));

                ((TextView) rootView.findViewById(R.id.v_user_rating))
                        .setText(clickedMovie.user_rating.toString() + "/10");

                ((TextView) rootView.findViewById(R.id.v_plot_synopsis))
                        .setText(clickedMovie.plot_synopsis);

                movieId = clickedMovie.id;

             /*   FetchTrailerTask trailerTask = new FetchTrailerTask();
                trailerTask.execute();

                FetchReviewTask reviewTask = new FetchReviewTask();
                reviewTask.execute();*/



                ((TextView) rootView.findViewById(R.id.v_duration)).setText(clickedMovie.id);


                //    setContentView(R.layout.fragment_detail);
                ViewGroup mScrollContent = (ViewGroup) rootView.findViewById(R.id.scroll_content);

               //bb View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_review_item, mScrollContent, false);



                //bbButton buttonView = (Button) layout2.findViewById(R.id.trailer_review_button);
                //bbTextView textView = (TextView) layout2.findViewById(R.id.trailer_review_text);
          //      String yy = mTrailerReviews[1].url;


                // This test dat is created since Async Task is not providing the data

                mTrailer[0] =   new trailerReviews (
                        TRAILER,
                        "sGbxmsDFVnE",
                        "YouTube",
                        "Official Trailer");

                mReviews[0] =new trailerReviews(
                        REVIEW,
                        "5675fd7792514179e7003dc5",
                        "http://j.mp/1ODjyR4",
                        "Frank Ochieng");

                mReviews[1] =new trailerReviews(
                        REVIEW,
                        "569bd8d7c3a36858c800046d",
                        "http://j.mp/1n4KbYz",
                        "bodokh");


                //End of Test Data

                if (mTrailer.length >= 1) {

                    TextView trailerLabel = new TextView(getActivity());
                    trailerLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    trailerLabel.setPadding(48, 8, 16, 8);
                    trailerLabel.setTextSize(16);
                    trailerLabel.setText("Trailers:");
                    mScrollContent.addView(trailerLabel);

                }

                for(int i=0; i < mTrailer.length; i++) {
                    View dynamicLayout = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_review_item, mScrollContent, false);
                    Button dynamicButton = (Button) dynamicLayout.findViewById(R.id.trailer_review_button);
                    TextView dynamicText = (TextView) dynamicLayout.findViewById(R.id.trailer_review_text);
                    dynamicButton.setText(getString(R.string.play_trailer));
                    dynamicText.setText(mTrailer[i].name_Author);
                    mScrollContent.addView(dynamicLayout);

                    View lineView = new View(getActivity());
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 4);
                    params.setMargins(48, 16, 48, 16);
                    lineView.setLayoutParams(params);
                    lineView.setBackgroundColor(Color.parseColor("#B3B3B3")); // darker_gray
                    mScrollContent.addView(lineView);

                }

                if (mReviews.length >= 1) {

                    TextView reviewsLabel = new TextView(getActivity());
                    reviewsLabel.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    reviewsLabel.setPadding(48, 8, 16, 8);
                    reviewsLabel.setTextSize(16);
                    reviewsLabel.setText("Reviews:");
                    mScrollContent.addView(reviewsLabel);
                }

                for(int i=0; i < mReviews.length; i++) {
                    View dynamicLayout = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_review_item, mScrollContent, false);
                    Button dynamicButton = (Button) dynamicLayout.findViewById(R.id.trailer_review_button);
                    TextView dynamicText = (TextView) dynamicLayout.findViewById(R.id.trailer_review_text);
                    dynamicButton.setText(getString(R.string.read_review));
                    dynamicText.setText("Review By: " + mReviews[i].name_Author);
                    mScrollContent.addView(dynamicLayout);

                    View lineView = new View(getActivity());
                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 4);
                    params.setMargins(48, 16, 48, 16);
                    lineView.setLayoutParams(params);
                    lineView.setBackgroundColor(Color.parseColor("#B3B3B3")); // darker_gray
                    mScrollContent.addView(lineView);
                }






            }
            Log.v("ONcreateView", "Create View : " + mtrailerReviews);
            return rootView;


        }
    }

    private trailerReviews[] getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        //final String LOG_TAG = getMovieDataFromJson.class.getSimpleName();

        final String LOG_TAG = "getTrailerDataFromJson";

        // These are the names of the JSON objects that need to be extracted.
        final String TRLR_RESULTS = "results";
        final String TRLR_TYPE = "type";
        final String TRLR_KEY = "key";
        final String TRLR_SITE = "site";
        final String TRLR_NAME = "name";


        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(TRLR_RESULTS);

        trailerReviews[] resultStrs = new trailerReviews[trailerArray.length()];

        for (int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject singleTrailer = trailerArray.getJSONObject(i);


            resultStrs[i] = new trailerReviews(
                    TRAILER,
                    singleTrailer.getString(TRLR_KEY),
                    singleTrailer.getString(TRLR_SITE),
                    singleTrailer.getString(TRLR_NAME));

        }


        return resultStrs;

    }

    public class FetchTrailerTask extends AsyncTask<String, Void, trailerReviews[]> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        @Override
        protected trailerReviews[] doInBackground(String... params) {

            // These two need to be declared outside the try / catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.

            // http://api.themoviedb.org/3/movie/140607/videos?api_key=1aa14e3bc0a68048c86623e99ebc2240

            String trailerJsonStr = null;
            String sortFormat = "popularity.desc";
            String apiId = getString(R.string.api_key);

            try {

                // Construct the URL for the moviedb query


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String TRAILER_PARAM = "videos";
                final String API_ID_PARAM = "api_key";


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(TRAILER_PARAM)
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

                trailerJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Trailer JSON String: " + trailerJsonStr);

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
                return getTrailerDataFromJson(trailerJsonStr);


            } catch (JSONException e) {

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(trailerReviews[] result) {
            if (result != null) {
                if (mTrailer != null) {

                    for (int i = 0; i < mTrailer.length; i++) {
                        mTrailer[i] = null;
                    }
                }
                //   mtrailerReviews.clear();
                mTrailer = result;
                //       for (reviewInfo singleTrailer : result) {
                //      mtrailerReviews.add(singleTrailer);
                //  }

            /*   if (mTrailer != null) {
                    mtrailerReviewsAdapter.clear();
                    for (trailerReviews singletrailerReview : mTrailer) {
                        mtrailerReviewsAdapter.add(singletrailerReview);
                    }
                }*/
            }
        }
    }

    public trailerReviews[] concat(trailerReviews[] firstArray, trailerReviews[] secondArray, trailerReviews[] thirdArray) {
        int aLen = firstArray.length;
        int bLen = secondArray.length;
        int cLen = thirdArray.length;

        trailerReviews[] resultArray = new trailerReviews[aLen + bLen + cLen];
        System.arraycopy(firstArray, 0, resultArray, 0, aLen);
        System.arraycopy(secondArray, 0, resultArray, aLen, bLen);
        System.arraycopy(thirdArray, 0, resultArray, aLen + bLen, cLen);
        return resultArray;
    }

    private trailerReviews[] getReviewDataFromJson(String reviewJsonStr)
            throws JSONException {

        //final String LOG_TAG = getMovieDataFromJson.class.getSimpleName();

        final String LOG_TAG = "getReviewDataFromJson";

        // These are the names of the JSON objects that need to be extracted.
        final String RVW_RESULTS = "results";
        final String RVW_TYPE = "type";
        final String RVW_ID = "id";
        final String RVW_URL = "url";
        final String RVW_AUTHOR = "author";


        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(RVW_RESULTS);

        trailerReviews[] resultStrs = new trailerReviews[reviewArray.length()];

        for (int i = 0; i < reviewArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject singleReview = reviewArray.getJSONObject(i);

            resultStrs[i] = new trailerReviews(
                    REVIEW,
                    singleReview.getString(RVW_ID),
                    singleReview.getString(RVW_URL),
                    singleReview.getString(RVW_AUTHOR));

        }

        return resultStrs;

    }

    public class FetchReviewTask extends AsyncTask<String, Void, trailerReviews[]> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        @Override
        protected trailerReviews[] doInBackground(String... params) {

            // These two need to be declared outside the try / catch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.

            // http://api.themoviedb.org/3/movie/140607/videos?api_key=1aa14e3bc0a68048c86623e99ebc2240

            String trailerJsonStr = null;
            String sortFormat = "popularity.desc";
            String apiId = getString(R.string.api_key);

            try {

                // Construct the URL for the moviedb query


                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String TRAILER_PARAM = "reviews";
                final String API_ID_PARAM = "api_key";


                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(TRAILER_PARAM)
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

                trailerJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Review JSON String: " + trailerJsonStr);

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
                return getReviewDataFromJson(trailerJsonStr);


            } catch (JSONException e) {

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(trailerReviews[] result) {
            if (result != null) {

                if (mReviews != null) {
                    for (int i = 0; i < mReviews.length; i++) {
                        mReviews[i] = null;
                    }
                }

                mReviews = result;

                mtrailerReviews = concat(mHeader, mTrailer, mReviews);
                Log.v(LOG_TAG, "TReview Array: " + mtrailerReviews);

             /*   if (mtrailerReviews != null) {
            //        mtrailerReviewsAdapter.clear();
                    for (trailerReviews singletrailerReview : mtrailerReviews) {
                        mtrailerReviewsAdapter.add(singletrailerReview);
                    }

                    //for (reviewInfo singleReview : result) {
                    //      mTrailerInfo.add(singleTrailer);

                }*/
            }
        }
    }
}