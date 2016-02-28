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

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int HIGHESTRATEDMOVIES = 200;
    static final int HIGHESTRATEDMOVIES_WITH_ID = 201;
    static final int FAVORITEMOVIES = 300;
    static final int FAVORITEMOVIES_WITH_ID = 301;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sHighestRatedMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteMoviesQueryBuilder;
    private static final String sMovieIdSelection =
            MovieContract.MovieListEntry.TABLE_NAME +
                    "." + MovieContract.MovieListEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String sHighestRatedMovieIdSelection =
            MovieContract.HighestRatedMovies.TABLE_NAME +
                    "." + MovieContract.HighestRatedMovies.COLUMN_MOVIE_ID + " = ? ";
    private static final String sFavoriteMovieIdSelection =
            MovieContract.FavoriteMovies.TABLE_NAME +
                    "." + MovieContract.FavoriteMovies.COLUMN_MOVIE_ID + " = ? ";

    static {

        sMoviesQueryBuilder = new SQLiteQueryBuilder();
        sMoviesQueryBuilder.setTables(
                MovieContract.MovieListEntry.TABLE_NAME);
    }

    static{

        sHighestRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
        sHighestRatedMoviesQueryBuilder.setTables(
                MovieContract.HighestRatedMovies.TABLE_NAME);
    }

    static {

        sFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteMoviesQueryBuilder.setTables(
                MovieContract.FavoriteMovies.TABLE_NAME);
    }

    private MovieDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_HIGHESTRATEDMOVIES, HIGHESTRATEDMOVIES);
        matcher.addURI(authority, MovieContract.PATH_HIGHESTRATEDMOVIES + "/*", HIGHESTRATEDMOVIES_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_FAVORITEMOVIES, FAVORITEMOVIES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITEMOVIES + "/*", FAVORITEMOVIES_WITH_ID);

        return matcher;
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {

        String movieId = MovieContract.MovieListEntry.getMovieIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{movieId};

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getHighestRatedMovieById(Uri uri, String[] projection, String sortOrder) {

        String movieId = MovieContract.HighestRatedMovies.getMovieIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{movieId};

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteMovieById(Uri uri, String[] projection, String sortOrder) {

        String movieId = MovieContract.FavoriteMovies.getMovieIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{movieId};

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieListEntry.CONTENT_TYPE;

            case MOVIE_WITH_ID:
                return MovieContract.MovieListEntry.CONTENT_ITEM_TYPE;

            case HIGHESTRATEDMOVIES:
                return MovieContract.HighestRatedMovies.CONTENT_TYPE;

            case HIGHESTRATEDMOVIES_WITH_ID:
                return MovieContract.HighestRatedMovies.CONTENT_ITEM_TYPE;

            case FAVORITEMOVIES:
                return MovieContract.FavoriteMovies.CONTENT_TYPE;

            case FAVORITEMOVIES_WITH_ID:
                return MovieContract.FavoriteMovies.CONTENT_ITEM_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

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

            case MOVIE_WITH_ID: {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }


            case HIGHESTRATEDMOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.HighestRatedMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case HIGHESTRATEDMOVIES_WITH_ID: {
                retCursor = getHighestRatedMovieById(uri, projection, sortOrder);
                break;
            }

            case FAVORITEMOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case FAVORITEMOVIES_WITH_ID: {
                retCursor = getFavoriteMovieById(uri, projection, sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieListEntry.buildMovieListUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case HIGHESTRATEDMOVIES: {

                long _id = db.insert(MovieContract.HighestRatedMovies.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.HighestRatedMovies.buildMovieListUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case FAVORITEMOVIES: {

                long _id = db.insert(MovieContract.FavoriteMovies.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavoriteMovies.buildMovieListUri();
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

            case HIGHESTRATEDMOVIES:
                        rowsDeleted = db.delete(
                                MovieContract.HighestRatedMovies.TABLE_NAME, selection, selectionArgs);
                        break;

            case FAVORITEMOVIES:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteMovies.TABLE_NAME, selection, selectionArgs);
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


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieListEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case HIGHESTRATEDMOVIES:
                rowsUpdated = db.update(MovieContract.HighestRatedMovies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case FAVORITEMOVIES:
                rowsUpdated = db.update(MovieContract.FavoriteMovies.TABLE_NAME, values, selection,
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
        int returnCount;

        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {

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

            case HIGHESTRATEDMOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.HighestRatedMovies.TABLE_NAME, null, value);
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

            case FAVORITEMOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.FavoriteMovies.TABLE_NAME, null, value);
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