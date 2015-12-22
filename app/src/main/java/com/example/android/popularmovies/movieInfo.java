package com.example.android.popularmovies;

/**
 * Created by yusuf on 21/12/15.
 */
public class movieInfo {

    String original_title ;
    String plot_synopsis ;
    Double user_rating ;
    String release_date;
    String poster_path_thumbnail ;

    public movieInfo(String vTitle,  String vSynopsis, Double vRating, String vReleasedate, String vPoster) {
        this.original_title =  vTitle;
        this.plot_synopsis = vSynopsis;
        this.user_rating = vRating;
        this.release_date = vReleasedate;
        this.poster_path_thumbnail = vPoster;
    }
}
