package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yusuf on 31/01/15.
 */
public class trailerInfo implements Parcelable{

    String key ;
    String site ;


    public trailerInfo(String vKey, String vSite) {
        this.key = vKey;
        this.site =  vSite;

    }

    private trailerInfo(Parcel in) {

        key = in.readString();
        site = in.readString();

    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel parcel,int i ) {

        parcel.writeString(key) ;
        parcel.writeString(site) ;


    }

    public static final Parcelable.Creator<trailerInfo> CREATOR = new Parcelable.Creator<trailerInfo>() {

        @Override
        public trailerInfo createFromParcel(Parcel parcel) {return  new trailerInfo(parcel);}

        @Override
        public trailerInfo[] newArray(int i) { return  new trailerInfo[i];}

    };
}
