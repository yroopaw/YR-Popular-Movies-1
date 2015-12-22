package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yusuf on 22/12/15.
 */
public class movieInfoAdapter extends ArrayAdapter <movieInfo>{

    private static  final String LOG_TAG = movieInfoAdapter.class.getSimpleName();

    public movieInfoAdapter(Activity context, List<movieInfo> movieList) {
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        movieInfo movieList  = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);

        ImageView movieThumbnail = (ImageView) rootView.findViewById(R.id.list_item_thumbnail_imageview);
       // movieThumbnail.setImageResource(movieList.poster_path_thumbnail);
      //  movieThumbnail.setImageResource(R.drawable.sample_3);

        Picasso.with(getContext()).load(movieList.poster_path_thumbnail).into(movieThumbnail);

        return rootView;
    }
}
