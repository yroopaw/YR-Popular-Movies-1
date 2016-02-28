package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

import static android.database.DatabaseUtils.dumpCursorToString;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment{

    //Added for Cusor Loader
    static final String DETAIL_URI = "URI";
    // These indices are tied to MOVIE_LIST_COLUMNS.  If MOVIE_LIST_COLUMNS changes, these must change.
    static final int COL_INDEX_ID = 0; // this for default column _ID
    static final int COL_MOVIE_ID = 1;
    static final int COL_POPULARITY = 2;
    static final int COL_ORIGINAL_TITLE = 3;
    static final int COL_PLOT_SYNOPSIS = 4;
    static final int COL_USER_RATING = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_POSTER_PATH_THUMBNAIL = 7;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    //Added for Sharing Functionality
    private static final String TRAILER_SHARE_HASTAG = " #PopularMoviesApp";
    private static final int DETAIL_LOADER = 0;
    int DETAIL_HEADER = 0;
    int TRAILER = 1;
    int REVIEW = 2;
    private trailerReviewsAdapter mtrailerReviewsAdapter;
    private GoogleApiClient client;
    private String mMovieId = null;
    private String mShareTrailerStr;
    //Added for Loader
    private ShareActionProvider mShareActionProvider;
    private movieInfo mclickedMovie;
    private Uri mUri;
    private ListView mListView;
    private Cursor mMovieCursor;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    // Fragment is added
    //                                  ------------------------------------------------------------------
    //                                  |                                                                |
    //                                  V                                                                |
    // onAttach() -> onCreate() -> OnCreateView() -> onActivityCreated() -> onStart() -> onResume()      |
    // Fragment is Active                                                                                |
    // When user navigates backward or fragment is removed  /replaced                                    |
    // The fragment is added to the back stack, then removed/replaced                                    |
    // onPause() -> on Stop() - > onDestroyView() --fragment returns to the layout from the back stack----
    //                                  |
    //                                  V
    //                            onDestroy() -> onDetach()
    // Fragment is Destroyed

    public static Uri getUriBasedOnPreferredSortOrder(Context context) {

        Uri sortOrderDbUri = MovieContract.MovieListEntry.buildMovieListUri();

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
        //zz  Bundle arguments = getArguments();
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    /*   setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            mMovieCursor = Utility.getMovieDetailsByUri(getActivity(), mUri);

            if (mMovieCursor != null && mMovieCursor.moveToFirst()) {
                mMovieId = mMovieCursor.getString(COL_MOVIE_ID);
                Log.v(LOG_TAG, " Detail Movie Cursor " + dumpCursorToString(mMovieCursor));
                Log.v(LOG_TAG, " Detail MovieId Cursor " + mMovieId);
                //   mMovieCursor.close();
            }
        }

        FetchTrailerTask trailerTask = new FetchTrailerTask(this);
        trailerTask.execute();

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }

        mtrailerReviewsAdapter = new trailerReviewsAdapter(getActivity(), new ArrayList<trailerReviews>());

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mListView = (ListView) rootView.findViewById(R.id.detail_scroll);
        mListView.setAdapter(mtrailerReviewsAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                trailerReviews trailerReviewData = (trailerReviews) adapterView.getItemAtPosition(position);

                //    Toast.makeText(getActivity(), "Item at Position #" + trailerReviewData.id + "# Clicked.", Toast.LENGTH_SHORT).show();

                if (trailerReviewData != null && trailerReviewData.type == TRAILER) {

                    String videoUrl = "https://www.youtube.com/watch?v=" + trailerReviewData.id;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d("trailer Play:", "Couldn't call " + videoUrl + ", no receiving apps installed!");
                    }
                }
                if (trailerReviewData != null && trailerReviewData.type == REVIEW) {

                    String reviewUrl = trailerReviewData.url;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrl));

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d("trailer Play:", "Couldn't call " + reviewUrl + ", no receiving apps installed!");
                    }
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //aa getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_detail, menu);
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        //Loader  ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            //  if(mclickedMovie != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
        else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareTrailerStr + TRAILER_SHARE_HASTAG);
        return shareIntent;

    }

    void onSortOrderChanged() {
        // replace the uri, since the sort order has changed
        mUri = Utility.getUriOfFirstMovieInPreferredSortOrder(getContext());
        if (null != mUri) {

            //   Uri updatedUri = MovieContract.MovieListEntry.buildMovieListSort(newSortType);
            //  Uri updatedUri = getUriBasedOnPreferredSortOrder(getContext());
            //  mUri = updatedUri;
            Log.v(LOG_TAG, "XYZ Entered detail activity sort order" + mUri);
            //  getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }


    }

    void onSortOrderChanged1(String newSortType) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {

            //   Uri updatedUri = MovieContract.MovieListEntry.buildMovieListSort(newSortType);
            Uri updatedUri = getUriBasedOnPreferredSortOrder(getContext());
            mUri = updatedUri;
            Log.v(LOG_TAG, "XYZ Entered detail activity sort order" + mUri);
            //  getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


// Fetch Trailer and Review Background Task start here

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

        trailerReviews[] resultStrs = new trailerReviews[trailerArray.length() + 1];

        if (mMovieCursor != null && mMovieCursor.moveToFirst()) {

            Log.v(LOG_TAG, " Detail Movie Cursor " + dumpCursorToString(mMovieCursor));
            Log.v(LOG_TAG, " Detail MovieId Cursor " + mMovieId);

            Log.v(LOG_TAG, " Detail TrMovie Cursor " + dumpCursorToString(mMovieCursor));
            resultStrs[0] = new trailerReviews(
                    DETAIL_HEADER,
                    null,
                    null,
                    null,
                    mMovieCursor.getInt(COL_POPULARITY),
                    mMovieCursor.getString(COL_ORIGINAL_TITLE),
                    mMovieCursor.getString(COL_PLOT_SYNOPSIS),
                    mMovieCursor.getDouble(COL_USER_RATING),
                    mMovieCursor.getString(COL_RELEASE_DATE),
                    mMovieCursor.getString(COL_POSTER_PATH_THUMBNAIL),
                    mMovieCursor.getString(COL_MOVIE_ID));
            mMovieCursor.close();
        }

        for (int i = 1; i < (trailerArray.length() + 1); i++) {
            // Get the JSON object representing the movie
            JSONObject singleTrailer = trailerArray.getJSONObject(i - 1);

            //Populate String for Sharing String first Trailer
            if (i == 1) {

                mShareTrailerStr = "Check out this interesting Movie " + "https://www.youtube.com/watch?v=" + singleTrailer.getString(TRLR_KEY);

                Log.v(LOG_TAG, " Share TrailerStr " + mShareTrailerStr);
            }


            resultStrs[i] = new trailerReviews(
                    TRAILER,
                    singleTrailer.getString(TRLR_KEY),
                    singleTrailer.getString(TRLR_SITE),
                    singleTrailer.getString(TRLR_NAME),
                    0, null, null, 0.0, null, null, null);
            Log.v(LOG_TAG, "TTY trailer info returned : " + singleTrailer);

        }


        return resultStrs;

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
                    singleReview.getString(RVW_AUTHOR),
                    0, null, null, 0.0, null, null, null);
        }

        return resultStrs;

    }

    public class FetchTrailerTask extends AsyncTask<String, Void, trailerReviews[]> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
        DetailActivityFragment container;
        public FetchTrailerTask(DetailActivityFragment f) {
            this.container = f;
        }

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
                        .appendPath(mMovieId)
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
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(trailerReviews[] result) {

            if (result != null) {
                mtrailerReviewsAdapter.clear();
                for (trailerReviews singleTrailer : result) {
                    mtrailerReviewsAdapter.add(singleTrailer);
                }
            }


            Log.v("Post Execute Trailer", "Count of Trailer items = " + result.length);

            mtrailerReviewsAdapter.notifyDataSetChanged();
            int yy = result.length;

            new FetchReviewTask().execute();

            }


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
                        .appendPath(mMovieId)
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

                for (trailerReviews singleReview : result) {
                    mtrailerReviewsAdapter.add(singleReview);
                }
            }


            Log.v("Post Execute Review", "Count of Review items = " + result.length);
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareTrailerIntent());
            }
            mtrailerReviewsAdapter.notifyDataSetChanged();


        }
    }
}
