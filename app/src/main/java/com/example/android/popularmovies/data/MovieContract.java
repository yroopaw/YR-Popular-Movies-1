package com.example.android.popularmovies.data;

/**
 * Created by yusuf on 07/02/16.
 */


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movieList";
    public static final String PATH_TRAILER_REVIEWS = "trailerReviewDb";



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
        public static final String COLUMN_MOVIE_ID = "id"; /* To be implemneted */

        // Type of Entry 0 = TRAILER 1 = REVIEW of Movie, Stored as int
        public static final String COLUMN_TYPE = "type";

        // YouTube video Key or unique key of review, Stored as String
        public static final String COLUMN_KEY = "key"; /* To rename id to key in adapter */

        // Site for trailer or url of review - Stored as String
        public static final String COLUMN_URL = "url";

        // Name of Trailer or Author of Review of Movie Stored as String
        public static final String COLUMN_NAME_AUTHOR = "name_Author";

        public static Uri buildTrailerReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }




    /* Inner class that defines the table contents of the weather table */
    public static final class MovieListEntry implements BaseColumns {


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

         //yy    return ContentUris.withAppendedId(CONTENT_URI, id);
        }



        public static Uri buildMovieTrailerReviews(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
/*
            Student: Fill in this buildWeatherLocation function
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
*/
    }
}