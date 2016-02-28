package com.example.android.popularmovies.data;

/**
 * Created by yusuf on 07/02/16.
 */


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.android.popularmovies.MovieFragment;
import com.example.android.popularmovies.R;


/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movieList";
    public static final String PATH_HIGHESTRATEDMOVIES = "highestRatedMoviesDb";
    public static final String PATH_FAVORITEMOVIES = "favoriteMoviesDb";
    private static final String LOG_TAG = MovieContract.class.getSimpleName();

   /* public static final String PATH_TRAILER_REVIEWS = "trailerReviewDb";



    public static final class TrailerReviewDbEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER_REVIEWS;


        //Table Name
        public static final String TABLE_NAME = "trailerReviewDb";


        // Column with the foreign key into the trailerReview table.
        //Original Movie Id as provided by themoviedb.org
        public static final String COLUMN_MOVIE_ID = "id";

        // Type of Entry 0 = TRAILER 1 = REVIEW of Movie, Stored as int
        public static final String COLUMN_TYPE = "type";

        // YouTube video Key or unique key of review, Stored as String
        public static final String COLUMN_KEY = "key";

        // Site for trailer or url of review - Stored as String
        public static final String COLUMN_URL = "url";

        // Name of Trailer or Author of Review of Movie Stored as String
        public static final String COLUMN_NAME_AUTHOR = "name_Author";

        public static Uri buildTrailerReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }*/

    public static final class MovieListEntry implements BaseColumns {

        //to rename this as highestRatedMovies
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table Name

        public static final String TABLE_NAME = "movieList";

        // Column with the foreign key into the TrailerReviewDb table.
        //Original Movie Id as provided by themoviedb.org Stored As String
        public static final String COLUMN_MOVIE_ID = "id";

        // Popularity of Movie Stored as int
        public static final String COLUMN_POPULARITY = "popularity";

        // Original Title of Movie Stored as String
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // Plot Synopsis of Movie Stored as String
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        // User rating of Movie Stored as Double
        public static final String COLUMN_USER_RATING = "user_rating";

        // Release Date of Movie Stored as String
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Path of Poster Thumbnail of Movie  as returned by API, to identify the thumbnail to be used
        public static final String COLUMN_POSTER_PATH_THUMBNAIL = "poster_path_thumbnail";

        public static Uri buildMovieListUri() {
            return MovieListEntry.CONTENT_URI;

        }


        public static Uri buildMovieListSort(String sortType) {
            String sortOrder = null;

            // if (sortType.equals(getActivity().getString(R.string.pref_sort_popular))) {
            if (sortType.equals("mostPopular")) {
                sortOrder = MovieContract.MovieListEntry.COLUMN_POPULARITY + " ASC";
                Log.v(LOG_TAG, "Sort: Order Popular" + sortOrder);
                // } else if (sortType.equals(getActivity().getString(R.string.pref_sort_highestrated))) {
            } else if (sortType.equals("highestRated")) {
                sortOrder = MovieContract.MovieListEntry.COLUMN_USER_RATING + " DESC";
                Log.v(LOG_TAG, "Sort: Order Popular" + sortOrder);
                // } else if (sortType.equals(getActivity().getString(R.string.pref_sort_favourite))) {
            } else if (sortType.equals("favouRite")) {
                Log.v(LOG_TAG, "Sort: Order Favorite");
                //TODO routine for Favorite
            } else {
                Log.d(LOG_TAG, "Sort Order Not Found:" + sortType);
            }


            //   Uri movieListFromDBQueryUri = MovieContract.MovieListEntry.buildMovieListUri();
            return CONTENT_URI.buildUpon().appendPath(sortOrder).build();
        }

        public static Uri selectedMovieQuery(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static Uri buildMovieTrailerReviews(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class HighestRatedMovies implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HIGHESTRATEDMOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHESTRATEDMOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HIGHESTRATEDMOVIES;

        //Table Name

        public static final String TABLE_NAME = "highestRatedMoviesDb";

        // Column with the foreign key into the TrailerReviewDb table.
        //Original Movie Id as provided by themoviedb.org Stored As String
        public static final String COLUMN_MOVIE_ID = "id";

        // Popularity of Movie Stored as int
        public static final String COLUMN_POPULARITY = "popularity";

        // Original Title of Movie Stored as String
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // Plot Synopsis of Movie Stored as String
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        // User rating of Movie Stored as Double
        public static final String COLUMN_USER_RATING = "user_rating";

        // Release Date of Movie Stored as String
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Path of Poster Thumbnail of Movie  as returned by API, to identify the thumbnail to be used
        public static final String COLUMN_POSTER_PATH_THUMBNAIL = "poster_path_thumbnail";

        public static Uri buildMovieListUri() {
            return HighestRatedMovies.CONTENT_URI;

        }

        public static Uri selectedMovieQuery(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static Uri buildMovieTrailerReviews(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class FavoriteMovies implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITEMOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITEMOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITEMOVIES;

        //Table Name

        public static final String TABLE_NAME = "favoriteMoviesDb";

        // Column with the foreign key into the TrailerReviewDb table.
        //Original Movie Id as provided by themoviedb.org Stored As String
        public static final String COLUMN_MOVIE_ID = "id";

        // Popularity of Movie Stored as int
        public static final String COLUMN_POPULARITY = "popularity";

        // Original Title of Movie Stored as String
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        // Plot Synopsis of Movie Stored as String
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        // User rating of Movie Stored as Double
        public static final String COLUMN_USER_RATING = "user_rating";

        // Release Date of Movie Stored as String
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // Path of Poster Thumbnail of Movie  as returned by API, to identify the thumbnail to be used
        public static final String COLUMN_POSTER_PATH_THUMBNAIL = "poster_path_thumbnail";

        public static Uri buildMovieListUri() {
            return FavoriteMovies.CONTENT_URI;

        }

        public static Uri selectedMovieQuery(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static Uri buildMovieTrailerReviews(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


}




