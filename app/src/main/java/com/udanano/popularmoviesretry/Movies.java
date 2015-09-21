package com.udanano.popularmoviesretry;

public class Movies {

    String poster;
    String title;
    String average;
    String overview;
    String release_date;
//default stuff
    public Movies(String vPoster, String vTitle, String vAverage, String vOverview, String vRelease_date)
    {
        this.poster = vPoster;
        this.title = vTitle;
        this.average = vAverage;
        this.overview = vOverview;
        this.release_date = vRelease_date;
    }

}
