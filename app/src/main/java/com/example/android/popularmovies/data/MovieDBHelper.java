package com.example.android.popularmovies.data;

/**
 * Created by yusuf on 07/02/16.
 */


import android.content.Context;



        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import com.example.android.popularmovies.data.MovieContract.TrailerReviewDbEntry;
        import com.example.android.popularmovies.data.MovieContract.MovieListEntry;


/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRAILERREVIEWS_TABLE = "CREATE TABLE " + TrailerReviewDbEntry.TABLE_NAME + " (" +
                TrailerReviewDbEntry.COLUMN_MOVIE_ID + " STRING PRIMARY KEY," +
                TrailerReviewDbEntry.COLUMN_TYPE + " INTEGAR UNIQUE NOT NULL, " +
                TrailerReviewDbEntry.COLUMN_KEY + " STRING NOT NULL, " +
                TrailerReviewDbEntry.COLUMN_URL + " STRING NOT NULL, " +
                TrailerReviewDbEntry.COLUMN_NAME_AUTHOR + " STRING NOT NULL " +
                " );";



        final String SQL_CREATE_MOVIELIST_TABLE = "CREATE TABLE " + MovieListEntry.TABLE_NAME + " (" +


                MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                MovieListEntry.COLUMN_MOVIE_ID + " STRING NOT NULL, " + /*check if int*/
                MovieListEntry.COLUMN_POPULARITY + " INTEGER NOT NULL, " +
                MovieListEntry.COLUMN_ORIGINAL_TITLE + " STRING NOT NULL, " +
                MovieListEntry.COLUMN_PLOT_SYNOPSIS + " STRING NOT NULL," +

                MovieListEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                MovieListEntry.COLUMN_RELEASE_DATE + " STRING NOT NULL, " +

                MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL + " STRING NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + MovieListEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                TrailerReviewDbEntry.TABLE_NAME + " (" + TrailerReviewDbEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieListEntry.COLUMN_MOVIE_ID +
               /* MovieListEntry.COLUMN_LOC_KEY + */ ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIELIST_TABLE);
    }





    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerReviewDbEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}