package com.udanano.popularmoviesretry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String poster = extras.getString("MOVIE_POSTER");
        String title = extras.getString("MOVIE_TITLE");
        String rating = extras.getString("MOVIE_RATING");
        String plot = extras.getString("MOVIE_PLOT");
        String release_date = extras.getString("MOVIE_RELEASE_DATE");

        ((TextView)findViewById(R.id.detail_title)).setText(title);
        ((TextView)findViewById(R.id.detail_rating)).setText(rating);
        ((TextView)findViewById(R.id.detail_plot)).setText(plot);
        ((TextView)findViewById(R.id.detail_release_date)).setText(release_date);

        String DB_base_Url = "http://image.tmdb.org/t/p/w185/";
        String builtUri = DB_base_Url += poster;

                Picasso.with(this)
                .load(builtUri)
                .into((ImageView) findViewById(R.id.detail_poster));

        //oh lord it works
    }
}