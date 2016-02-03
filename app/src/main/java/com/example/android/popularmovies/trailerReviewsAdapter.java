package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by yusuf on 02/02/15.
 */
public class trailerReviewsAdapter extends ArrayAdapter<trailerReviews> {

    private static final String LOG_TAG = movieInfoAdapter.class.getSimpleName();

    public trailerReviewsAdapter(Activity context, List<trailerReviews> trailerReviewsList) {
        super(context, 0, trailerReviewsList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        trailerReviews trailerReviewsList = getItem(position);
        //View rootView = LayoutInflater.from(getContext()).inflate(R.layout.movie_detail, parent, false);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailerreview, parent, false);

        TextView tv = (TextView) rootView.findViewById(R.id.list_item_trailerreview_text);

        tv.setText(trailerReviewsList.name_Author);
     /*   ImageView movieThumbnail = (ImageView) rootView.findViewById(R.id.list_item_thumbnail_imageview);


        //Build URl for the Poster Thumbnail
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

        String posterUrl = POSTER_BASE_URL + getContext().getString(R.string.poster_resolution) + "/" + movieList.poster_path_thumbnail;


        Picasso.with(getContext()).load(posterUrl).into(movieThumbnail);*/


        return rootView;
    }


}
