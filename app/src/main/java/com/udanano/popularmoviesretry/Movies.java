package com.udanano.popularmoviesretry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Movies implements Comparable <Movies>, Parcelable {

    String poster;
    String title;
    String average;
    String overview;
    String release_date;
    String popularity;
    String id;
//default stuff
    public Movies(String vPoster, String vTitle, String vAverage, String vOverview, String vRelease_date, String vPopularity, String vId)
    {
        this.poster = vPoster;
        this.title = vTitle;
        this.average = vAverage;
        this.overview = vOverview;
        this.release_date = vRelease_date;
        this.popularity = vPopularity;
        this.id = vId;
    }
    //parcelable thingy. better make sure these are in the correct order.
    private Movies(Parcel in)
    {
        poster = in.readString();
        title = in.readString();
        average = in.readString();
        overview = in.readString();
        release_date = in.readString();
        popularity = in.readString();
        id = in.readString();
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

    @Override
    public int describeContents(){return 0;}

    @Override
    public void writeToParcel(Parcel parcel, int i){}

    public final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel parcel) {
            return new Movies(parcel);
        }

        @Override
        public Movies[] newArray(int i) {
            return new Movies[i];
        }
    };
}
