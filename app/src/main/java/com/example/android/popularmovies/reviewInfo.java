package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yusuf on 01/02/15.
 */
public class reviewInfo implements Parcelable{

    String id ;
    String author ;
  //  String content;
    String url;


    public reviewInfo(String vId, String vAuthor, String vUrl) {
        this.id = vId;
        this.author =  vAuthor;
        //this.content = vContent
        this.url = vUrl;

    }

    private reviewInfo(Parcel in) {

        id = in.readString();
        author = in.readString();
        // content = in.readString();
        url = in.readString();

    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel,int i ) {

        parcel.writeString(id) ;
        parcel.writeString(author) ;
        //parcel.writeString(content);
        parcel.writeString(url);


    }

    public static final Parcelable.Creator<reviewInfo> CREATOR = new Parcelable.Creator<reviewInfo>() {

        @Override
        public reviewInfo createFromParcel(Parcel parcel) {return  new reviewInfo(parcel);}

        @Override
        public reviewInfo[] newArray(int i) { return  new reviewInfo[i];}

    };
}
