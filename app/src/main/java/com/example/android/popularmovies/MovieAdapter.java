package com.example.android.popularmovies;

/**
 * Created by yusuf on 10/02/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * {@link MovieAdapter} exposes a list of movies from a
 * {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */

public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    //This is ported from FetchMovieTask

    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
//   yy     int idx_poster_path_thumbnail = cursor.getColumnIndex(MovieContract.MovieListEntry.COLUMN_POSTER_PATH_THUMBNAIL);


        //yy return cursor.getString(idx_poster_path_thumbnail);
        return cursor.getString(MovieFragment.COL_POSTER_PATH_THUMBNAIL);
    }


    //Remember that these views are reused as needed.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

        return view;
    }

    //This is where we fill-in the views with the contents of the cursor.


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        ImageView movieThumbnail = (ImageView) (view);


        //Build URl for the Poster Thumbnail
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        String posterUrl;

        SharedPreferences pref = mContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Boolean twoPane = pref.getBoolean("mTwoPane_key", false);


        if (twoPane) {
            posterUrl = POSTER_BASE_URL + context.getString(R.string.poster_resolution_two_pane) + "/" + convertCursorRowToUXFormat(cursor);
        } else {
            posterUrl = POSTER_BASE_URL + context.getString(R.string.poster_resolution_one_pane) + "/" + convertCursorRowToUXFormat(cursor);
        }


        Picasso.with(context).load(posterUrl).into(movieThumbnail);

    }
}
