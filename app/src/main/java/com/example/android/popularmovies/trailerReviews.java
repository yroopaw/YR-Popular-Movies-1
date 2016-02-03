package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yusuf on 01/02/15.
 */
public class trailerReviews implements Parcelable{

    int type; // Will define wether record is Trailer (0) or Review(1)
    String id; // will be youtube key for trailer or id of review
    String url; // will be site for trailer or url of review
    String name_Author ; // will be name of trailer or author of review


    public trailerReviews(int vType, String vId, String vUrl, String vName_Author) {
        this.type = vType;
        this.id = vId;
        this.url = vUrl;
        this.name_Author =  vName_Author;


    }

    private trailerReviews(Parcel in) {

        type = in.readInt();
        id = in.readString();
        url = in.readString();
        name_Author = in.readString();

    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel,int i ) {

        parcel.writeInt(type);
        parcel.writeString(id) ;
        parcel.writeString(url);
        parcel.writeString(name_Author) ;


    }

    public static final Parcelable.Creator<trailerReviews> CREATOR = new Parcelable.Creator<trailerReviews>() {

        @Override
        public trailerReviews createFromParcel(Parcel parcel) {return  new trailerReviews(parcel);}

        @Override
        public trailerReviews[] newArray(int i) { return  new trailerReviews[i];}

    };
}
