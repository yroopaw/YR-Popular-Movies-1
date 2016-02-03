package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yusuf on 21/12/15.
 */
public class movieInfo implements Parcelable{

    int    popularity;
    String original_title ;
    String plot_synopsis ;
    Double user_rating ;
    String release_date;
    String poster_path_thumbnail ;
    String id;

    public movieInfo(int vPopularity, String vTitle,  String vSynopsis, Double vRating,
                     String vReleasedate, String vPoster, String vId) {
        this.popularity = vPopularity;
        this.original_title =  vTitle;
        this.plot_synopsis = vSynopsis;
        this.user_rating = vRating;
        this.release_date = vReleasedate;
        this.poster_path_thumbnail = vPoster;
        this.id = vId;
    }

    private movieInfo(Parcel in) {
        popularity = in.readInt();
        original_title = in.readString();
        plot_synopsis = in.readString();
        user_rating = in.readDouble();
        release_date = in.readString();
        poster_path_thumbnail = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel,int i ) {
        parcel.writeInt(popularity);
        parcel.writeString(original_title) ;
        parcel.writeString(plot_synopsis) ;
        parcel.writeDouble(user_rating) ;
        parcel.writeString(release_date);
        parcel.writeString(poster_path_thumbnail) ;
        parcel.writeString(id);

    }

    public static final Parcelable.Creator<movieInfo> CREATOR = new Parcelable.Creator<movieInfo>() {

        @Override
        public movieInfo createFromParcel(Parcel parcel) {return  new movieInfo(parcel);}

        @Override
        public movieInfo[] newArray(int i) { return  new movieInfo[i];}

    };
}
