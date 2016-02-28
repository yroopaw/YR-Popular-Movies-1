package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;

/**
 * Created by yusuf on 27/02/16.
 */
public class Utility {

    // These indices are tied to MOVIE_LIST_COLUMNS.  If MOVIE_LIST_COLUMNS changes, these must change.
    static final int COL_INDEX_ID = 0; // this for default column _ID
    static final int COL_MOVIE_ID = 1;
    static final int COL_POPULARITY = 2;
    static final int COL_ORIGINAL_TITLE = 3;
    static final int COL_PLOT_SYNOPSIS = 4;
    static final int COL_USER_RATING = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_POSTER_PATH_THUMBNAIL = 7;

    public static void favouriteButtonClicked(Context context, trailerReviews movieDetail) {

        Boolean wasAddedToFavorite = addFavoriteMovie(context, movieDetail);

        Log.v("Fav Button", "Fav Button Clicked ");

        if (wasAddedToFavorite) {
            Toast.makeText(context, "Movie " + movieDetail.original_title + " Marked As Favourite Movie", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Movie " + movieDetail.original_title + " Is Already Marked As Favourite", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Helper method to handle insertion of a new movie in the favorite database.
     *
     * @return the row ID of the added location.
     */

    public static Boolean addFavoriteMovie(Context context, trailerReviews movieData) {

        Boolean markedFavorite;


        Uri favoriteTableUri = MovieContract.FavoriteMovies.buildMovieListUri();

        Cursor favoriteCursor = context.getContentResolver().query(favoriteTableUri,
                null,
                MovieContract.FavoriteMovies.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieData.movieId},
                null);


        // First, check if the movie id name exists in the favorite db

        if (favoriteCursor.moveToFirst()) {
            // int movieIdIndex = favoriteCursor.getColumnIndex(MovieContract.FavoriteMovies.COLUMN_MOVIE_ID);
            //  insertedMovieRowId = favoriteCursor.getLong(movieIdIndex);
            markedFavorite = false;

        } else {
            // Now that the content provider is set up, insert row of data
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieDetailValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_MOVIE_ID, movieData.movieId);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_POPULARITY, movieData.popularity);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_ORIGINAL_TITLE, movieData.original_title);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_PLOT_SYNOPSIS, movieData.plot_synopsis);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_USER_RATING, movieData.user_rating);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_RELEASE_DATE, movieData.release_date);
            movieDetailValues.put(MovieContract.FavoriteMovies.COLUMN_POSTER_PATH_THUMBNAIL, movieData.poster_path_thumbnail);


            Uri insertedUri = context.getContentResolver().insert(
                    MovieContract.FavoriteMovies.CONTENT_URI,
                    movieDetailValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            //  insertedMovieRowId = ContentUris.parseId(insertedUri);
            markedFavorite = true;

        }

        favoriteCursor.close();


        return markedFavorite;
    }

    public static Cursor getMovieDetailsByUri(Context context, Uri uri) {


        String[] selectionArgs;
        String selection = MovieContract.MovieListEntry.COLUMN_MOVIE_ID + " = ?";
        Uri currentUri = MovieContract.MovieListEntry.buildMovieListUri();

        String currentDB = uri.getPathSegments().get(0);
        String movieId = uri.getPathSegments().get(1);

        selectionArgs = new String[]{movieId};


        if (currentDB.equals(MovieContract.MovieListEntry.TABLE_NAME)) {
            currentUri = MovieContract.MovieListEntry.buildMovieListUri();
            selection = MovieContract.MovieListEntry.COLUMN_MOVIE_ID + " = ?";
        } else if (currentDB.equals(MovieContract.HighestRatedMovies.TABLE_NAME)) {
            currentUri = MovieContract.HighestRatedMovies.buildMovieListUri();
            selection = MovieContract.HighestRatedMovies.COLUMN_MOVIE_ID + " = ?";
        } else if (currentDB.equals(MovieContract.FavoriteMovies.TABLE_NAME)) {
            currentUri = MovieContract.FavoriteMovies.buildMovieListUri();
            selection = MovieContract.FavoriteMovies.COLUMN_MOVIE_ID + " = ?";
        } else {
            Log.v("getMovieDetailBySort", "Table Selection Error : - " + currentDB);
        }


        Cursor selectedMovieCursor = context.getContentResolver().query(currentUri,
                null,
                selection,
                selectionArgs,
                null);


        return selectedMovieCursor;
    }


    public static Uri getUriOfFirstMovieInPreferredSortOrder(Context context) {

        final String LOG_TAG = "getUriOfFirstMovieInPreferredSortOrder";
        Uri sortOrderDbUri = MovieContract.MovieListEntry.buildMovieListUri(); //Defualt to popular movies
        String firstMovieId = "135397"; // Default to a dummy movie

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


        } else {
            Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
        }


        Cursor cursor = context.getContentResolver().query(sortOrderDbUri,
                null,
                null,
                null,
                null);

        if ((cursor != null) && cursor.moveToFirst()) {
            firstMovieId = cursor.getString(COL_MOVIE_ID); // Movie Id
        }

        cursor.close();


        return sortOrderDbUri.buildUpon().appendPath(firstMovieId).build();


    }

}
