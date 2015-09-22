package com.udanano.popularmoviesretry;

import java.util.Comparator;

public class Movies implements Comparable <Movies> {

    String poster;
    String title;
    String average;
    String overview;
    String release_date;
    String popularity;
//default stuff
    public Movies(String vPoster, String vTitle, String vAverage, String vOverview, String vRelease_date, String vPopularity)
    {
        this.poster = vPoster;
        this.title = vTitle;
        this.average = vAverage;
        this.overview = vOverview;
        this.release_date = vRelease_date;
        this.popularity = vPopularity;
    }

    public String getPopularity(){
        return popularity;
    }

    public int compareTo(Movies compareMovies) {

        return 0;
    }

    public static Comparator<Movies> MovieRatingComparator
            = new Comparator<Movies>() {
        @Override
        public int compare(Movies lhs, Movies rhs) {
            return rhs.average.compareTo(lhs.average);
        }
    };

    public static Comparator<Movies> MoviePopularityComparator
            = new Comparator<Movies>() {
        @Override
        public int compare(Movies lhs, Movies rhs) {
            return rhs.popularity.compareTo(lhs.popularity);
        }
    };
}
