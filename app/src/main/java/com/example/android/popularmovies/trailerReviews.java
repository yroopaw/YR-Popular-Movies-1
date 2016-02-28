package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yusuf on 01/02/15.
 */
public class trailerReviews implements Parcelable{

    public static final Parcelable.Creator<trailerReviews> CREATOR = new Parcelable.Creator<trailerReviews>() {

        @Override
        public trailerReviews createFromParcel(Parcel parcel) {
            return new trailerReviews(parcel);
        }

        @Override
        public trailerReviews[] newArray(int i) {
            return new trailerReviews[i];
        }

    };
    int type; // Will define wether record is Trailer (0) or Review(1)
    String id; // will be youtube key for trailer or id of review
    String url; // will be site for trailer or url of review
    String name_Author ; // will be name of trailer or author of review
    //Added to pass movie information
    int popularity;
    String original_title;
    String plot_synopsis;
    Double user_rating;
    String release_date;
    String poster_path_thumbnail;
    String movieId;

    public trailerReviews(int vType, String vId, String vUrl, String vName_Author,
                          int vPopularity, String vOriginal_title, String vPlot_synopsis, Double vUser_rating,
                          String vRelease_date, String vPoster_path_thumbnail, String vMovieId) {
        this.type = vType;
        this.id = vId;
        this.url = vUrl;
        this.name_Author =  vName_Author;
        //Added to for movie information
        this.popularity = vPopularity;
        this.original_title = vOriginal_title;
        this.plot_synopsis = vPlot_synopsis;
        this.user_rating = vUser_rating;
        this.release_date = vRelease_date;
        this.poster_path_thumbnail = vPoster_path_thumbnail;
        this.movieId = vMovieId;


    }

    private trailerReviews(Parcel in) {

        type = in.readInt();
        id = in.readString();
        url = in.readString();
        name_Author = in.readString();
        //Added  for movie information
        popularity = in.readInt();
        original_title = in.readString();
        plot_synopsis = in.readString();
        user_rating = in.readDouble();
        release_date = in.readString();
        poster_path_thumbnail = in.readString();
        movieId = in.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel,int i ) {

        parcel.writeInt(type);
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeString(name_Author);
        //Added  for movie information
        parcel.writeInt(popularity);
        parcel.writeString(original_title);
        parcel.writeString(plot_synopsis);
        parcel.writeDouble(user_rating);
        parcel.writeString(release_date);
        parcel.writeString(poster_path_thumbnail);
        parcel.writeString(movieId);


    }
}
