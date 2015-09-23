package com.udanano.popularmoviesretry;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Movies> {
    // change this to ArrayAdapter<Movies> so it accepts objects of Movies type
    //maybe rename this to MovieAdapter since it's going to do more than movies maybe, idk yet

    private Context mContext;
    public static String[] myPosters;

public ImageAdapter(Activity context, List<Movies> data) {
    //super(context, R.layout.fragment_main, data);
    super(context, 0, data);
    this.mContext = context;

}
    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
//        Log.v("Steps", "Overriding getView");
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setAdjustViewBounds(true);

        } else {
            imageView = (ImageView) convertView;
        }

        String DB_base_Url = "http://image.tmdb.org/t/p/w185/";


        Movies movies = getItem(position);
        String builtUri = DB_base_Url += movies.poster;

        Picasso.with(mContext)
                .load(builtUri)
                .into(imageView);

        return imageView;
    }
}