package com.example.android.popularmovies.data;

/**
 * Created by yusuf on 07/02/16.
 */


import android.content.ContentValues;



        import android.annotation.TargetApi;
        import android.content.ContentProvider;
        import android.content.ContentValues;
        import android.content.UriMatcher;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteQueryBuilder;
        import android.net.Uri;

import java.net.URI;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_TRAILER_REVIEWS = 101;
   // static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int TRAILER_REVIEWS = 300;

    private static final SQLiteQueryBuilder sMoviesWithTrailerReviewsQueryBuilder;

    static{
        sMoviesWithTrailerReviewsQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //movie INNER JOIN location ON MovieListEntry.id = TrailerReviewEntry.id
        sMoviesWithTrailerReviewsQueryBuilder.setTables(
                MovieContract.MovieListEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerReviewDbEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieListEntry.TABLE_NAME +
                        "." + MovieContract.MovieListEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailerReviewDbEntry.TABLE_NAME +
                        "." + MovieContract.TrailerReviewDbEntry.COLUMN_MOVIE_ID);
    }

    //TrailerReviewEntry.id = ?
    private static final String sLocationSettingSelection =
            MovieContract.TrailerReviewDbEntry.TABLE_NAME+
                    "." + MovieContract.TrailerReviewDbEntry.COLUMN_MOVIE_ID + " = ? ";

    /* Not needed in popular movies
    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            MovieContract.TrailerReviewDbEntry.TABLE_NAME+
                    "." + MovieContract.TrailerReviewDbEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    MovieContract.MovieListEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            MovieContract.TrailerReviewDbEntry.TABLE_NAME +
                    "." + MovieContract.TrailerReviewDbEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    MovieContract.MovieListEntry.COLUMN_DATE + " = ? ";


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = MovieContract.MovieListEntry.getLocationSettingFromUri(uri);
        long startDate = MovieContract.MovieListEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sMoviesWithTrailerReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getWeatherByLocationSettingAndDate(

            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = MovieContract.MovieListEntry.getLocationSettingFromUri(uri);
        long date = MovieContract.MovieListEntry.getDateFromUri(uri);

        return sMoviesWithTrailerReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

*/
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_TRAILER_REVIEWS);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, MovieContract.PATH_TRAILER_REVIEWS, TRAILER_REVIEWS);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case MOVIE:
                return MovieContract.MovieListEntry.CONTENT_TYPE;
            case TRAILER_REVIEWS:
                return MovieContract.TrailerReviewDbEntry.CONTENT_TYPE;
            case MOVIE_WITH_TRAILER_REVIEWS:
                return MovieContract.TrailerReviewDbEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "Movie/*/*"
            case MOVIE_WITH_TRAILER_REVIEWS:
            {
      //          retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
          /*  // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }*/
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Trailer review"
            case TRAILER_REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerReviewDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
            //    normalizeDate(values);
                long _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieListEntry.buildMovieListUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER_REVIEWS: {
             //   normalizeDate(values);
                long _id = db.insert(MovieContract.TrailerReviewDbEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieListEntry.buildMovieListUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
                case MOVIE:
                        rowsDeleted = db.delete(
                                        MovieContract.MovieListEntry.TABLE_NAME, selection, selectionArgs);
                        break;
                case TRAILER_REVIEWS:
                        rowsDeleted = db.delete(
                                        MovieContract.TrailerReviewDbEntry.TABLE_NAME, selection, selectionArgs);
                        break;
                default:
                        throw new UnsupportedOperationException("Unknown uri: " + uri);
                }
        // Because a null deletes all rows
                if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        return rowsDeleted;
    }

 /*   private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieContract.MovieListEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(MovieContract.MovieListEntry.COLUMN_DATE);
            values.put(MovieContract.MovieListEntry.COLUMN_DATE, MovieContract.normalizeDate(dateValue));
        }
    }*/

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
                case MOVIE:
                       // normalizeDate(values);
                        rowsUpdated = db.update(MovieContract.MovieListEntry.TABLE_NAME, values, selection,
                                        selectionArgs);
                        break;
                case TRAILER_REVIEWS:
                        rowsUpdated = db.update(MovieContract.TrailerReviewDbEntry.TABLE_NAME, values, selection,
                                        selectionArgs);
                        break;
                default:
                        throw new UnsupportedOperationException("Unknown uri: " + uri);
                }
        if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        return rowsUpdated;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}