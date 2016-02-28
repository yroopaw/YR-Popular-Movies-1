package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yusuf on 02/02/15.
 */
public class trailerReviewsAdapter extends ArrayAdapter<trailerReviews> {

    private static final String LOG_TAG = movieInfoAdapter.class.getSimpleName();
    private Context mContext;


    public trailerReviewsAdapter(Activity context, List<trailerReviews> trailerReviewsList) {

        super(context, 0, trailerReviewsList);
        this.mContext = context;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView;
        final int DETAIL_HEADER = 0;
        final int TRAILER = 1;
        final int REVIEW = 2;
        final String trailerReviewIcon;

        final trailerReviews trailerReviewsList = getItem(position);

        if (position == 0) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.movie_detail_header, parent, false);
            //  LinearLayout tv = (LinearLayout) rootView.findViewById(R.id.movie_detail_list_header);

            ((TextView) rootView.findViewById(R.id.v_original_title))
                    .setText(trailerReviewsList.original_title);

            final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";


            String posterUrl;

            SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            Boolean twoPane = pref.getBoolean("mTwoPane_key", false);


            if (twoPane) {
                posterUrl = POSTER_BASE_URL + getContext().getString(R.string.poster_resolution_two_pane) + "/" + trailerReviewsList.poster_path_thumbnail;
            } else {
                posterUrl = POSTER_BASE_URL + getContext().getString(R.string.poster_resolution_one_pane) + "/" + trailerReviewsList.poster_path_thumbnail;
            }
            //     String posterUrl = POSTER_BASE_URL + getContext().getString(R.string.poster_resolution)
            //           + "/" + trailerReviewsList.poster_path_thumbnail;

            ImageView movieThumbnail = (ImageView) rootView.findViewById(R.id.v_poster_thumbnail);
            Picasso.with(getContext()).load(posterUrl).into(movieThumbnail);


            ((TextView) rootView.findViewById(R.id.v_release_date))
                    .setText((trailerReviewsList.release_date).substring(0, 4));

            ((TextView) rootView.findViewById(R.id.v_user_rating))
                    .setText(Double.toString(trailerReviewsList.user_rating) + "/10");

            ((TextView) rootView.findViewById(R.id.v_plot_synopsis))
                    .setText(trailerReviewsList.plot_synopsis);


            ((TextView) rootView.findViewById(R.id.v_duration)).setText(trailerReviewsList.movieId);


            Button favButton = (Button) rootView.findViewById(R.id.fav_button);

            favButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int yy = 1;
                    Log.v("Fav Buttom", "Fav Button Clicked in Adapter");
                    Utility.favouriteButtonClicked(mContext, trailerReviewsList);

                }

            });


        } else {

            rootView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_review_item, parent, false);

            ((TextView) rootView.findViewById(R.id.trailer_review_text))
                    .setText(trailerReviewsList.name_Author);

            switch (trailerReviewsList.type) {
                case DETAIL_HEADER: {
                    throw new UnsupportedOperationException("Trying to display buttons for Movie Header: " + trailerReviewsList.type);

                }
                case TRAILER: {
                    trailerReviewIcon = getContext().getString(R.string.play_trailer);
                    break;
                }
                case REVIEW: {
                    trailerReviewIcon = getContext().getString(R.string.read_review);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown Movie Detail Adapter List Type: " + trailerReviewsList.type);
            }

            ((TextView) rootView.findViewById(R.id.trailer_review_button))
                    .setText(trailerReviewIcon);
        }

        return rootView;
    }


}
