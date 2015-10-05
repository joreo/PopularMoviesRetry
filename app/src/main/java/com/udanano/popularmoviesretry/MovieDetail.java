package com.udanano.popularmoviesretry;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends ActionBarActivity {

    public static String[] trailerCode = new String[15];
    public static List<String> reviews = new ArrayList<String>();
    public static int reviewLength;
    public static boolean falseData = false;
    public static String poster;
    public static String title;
    public static String rating;
    public static String plot;
    public static String release_date;

    public static String popularity;

    //also used in some of the other functions
    //public static String movie_id ;
    //public static String API_KEY = "fcece545ee341e673a9344a531f80064";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
        setContentView(R.layout.detail_main);


        //oh lord it works
        //edit 9/29: it work"ed" until I tried to add trailer data/adapter
        //edit works again! got the name:trailer thing fixed - boom
        //scrollview problems - boom
        //add reviews - boom
        //fav button
        //worked fine with just phone. issues making the double panel for tablets including:
        //      crash w/o default data
        //      can't get my variables to stay alive after the if
        //      i'm bad at fragments/frames. learn2fragmentpls
        //      click a thing - watch it take over the whole screen. good job, dumbass.
    }

    public static class DetailFragment extends Fragment {
        private final String LOG_TAG = MovieDetail.class.getSimpleName();
        private ArrayAdapter<String> mTrailerAdapter;
        private ArrayAdapter<String> mReviewAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            try {
                Log.e("args?", this.getArguments().toString());
                    intent.putExtras(this.getArguments());

            } catch (Exception e) {}



            if (intent.hasExtra("MOVIE_POSTER")) {
                //stop making API calls if all I did was open the app.
                FetchTrailer trailer = new FetchTrailer();
                trailer.execute();
            }
            mTrailerAdapter =
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.list_item_trailers,
                            R.id.list_item_trailer_textview,
                            new ArrayList<String>()
                            );



            View rootView = inflater.inflate(R.layout.detail_main, container, false);




            //Setup this way to handle non-set extras
            final String poster = intent.getStringExtra("MOVIE_POSTER");
            final String title = intent.getStringExtra("MOVIE_TITLE");
            final String rating = intent.getStringExtra("MOVIE_RATING");
            final String plot = intent.getStringExtra("MOVIE_PLOT");
            final String release_date = intent.getStringExtra("MOVIE_RELEASE_DATE");
            final String movie_id = intent.getStringExtra("MOVIE_ID");
            final String popularity = intent.getStringExtra("MOVIE_POP");

            final Button favoriteButton = (Button) rootView.findViewById(R.id.favoriteButton);


            if (!intent.hasExtra("API_KEY")) {
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg")
                        .into((ImageView) rootView.findViewById(R.id.detail_poster));
                ((TextView) rootView.findViewById(R.id.detail_title)).setText("Welcome!");
                ((TextView) rootView.findViewById(R.id.detail_rating)).setText("This is default data!");
                ((TextView) rootView.findViewById(R.id.detail_plot)).setText("Click something to get started");
                ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(":)");
                favoriteButton.setEnabled(false);
                Log.e("hasExtra", "false");
            } else {
                ((TextView) rootView.findViewById(R.id.detail_title)).setText(title);
                ((TextView) rootView.findViewById(R.id.detail_rating)).setText("Rating: " + rating);
                ((TextView) rootView.findViewById(R.id.detail_plot)).setText(plot);
                ((TextView) rootView.findViewById(R.id.detail_release_date)).setText("Released: " + release_date);
                favoriteButton.setEnabled(true);
                Log.e("hasExtra", "true");


                String DB_base_Url = "http://image.tmdb.org/t/p/w185/";
                String builtUri = DB_base_Url += poster;

                Picasso.with(getActivity())
                        .load(builtUri)
                        .into((ImageView) rootView.findViewById(R.id.detail_poster));
            }

            ListView listView = (ListView) rootView.findViewById(R.id.listview_trailers);
            //favorite button

            //search the db for the movie id
            //if it exists,
            //
            if (intent.hasExtra("API_KEY")) {
                DatabaseOps DB = new DatabaseOps(getActivity());
                Cursor CR = DB.findEntry(DB, movie_id);
                int cursorCount = CR.getCount();
                String cursorString;
                if (cursorCount != 0) {
                    CR.moveToFirst();
                    cursorString = CR.getString(0);
                    do {
                        if (movie_id.equals(cursorString)) {
                            favoriteButton.setText("Remove from Favorites");
                        }
                    } while (CR.moveToNext());
                }
                DB.close();
            }
            //i'm hoping this will keep trailers, reviews, and fave button from working with the initial dummy data load
            //if(intent.hasExtra("API_KEY")) {
                View.OnClickListener myHandler = new View.OnClickListener() {
                    public void onClick(View v) {
                        DatabaseOps DB = new DatabaseOps(getActivity());
                        if (favoriteButton.getText().equals("Add to Favorites")) {
                            //add to favorites
                            DB.addEntry(DB, poster, title, rating, plot, release_date, popularity, movie_id);
                            Toast.makeText(getActivity(), "Added " + title + " to favorites.", Toast.LENGTH_LONG).show();
                            //change button text
                            favoriteButton.setText("Remove from Favorites");
                        } else {
                            //remove from favorites
                            DB.deleteEntry(DB, movie_id);
                            Toast.makeText(getActivity(), "Removed " + title + " from favorites.", Toast.LENGTH_LONG).show();
                            //change button text
                            favoriteButton.setText("Add to Favorites");
                        }
                        DB.close();
                    }
                };
            favoriteButton.setOnClickListener(myHandler);



                listView.setAdapter(mTrailerAdapter);
                //reviews adapter setting
                mReviewAdapter =
                        new ArrayAdapter<String>(
                                getActivity(),
                                R.layout.list_item_reviews,
                                R.id.list_item_review_textview,
                                new ArrayList<String>()
                        );

                ListView reviewListView = (ListView) rootView.findViewById(R.id.listview_reviews);
                reviewListView.setAdapter(mReviewAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String videoId = mTrailerAdapter.getItem(position);
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerCode[position]));
                            intent.putExtra("VIDEO_ID", trailerCode[position]);
                            Log.e("@@@clicker", videoId + " " + trailerCode[position]);
                            startActivity(intent);

                        } catch (Exception e) {
                            //consider launching a browser via intent if the youtube app is missing
                            String url = "http://youtu.be/" + trailerCode[position];
                            Toast.makeText(getActivity(), "Consider installing the YouTube app to see " + url, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }
                });
            //}
            //end of if for dummy data

            return rootView;

        }

        public class FetchTrailer extends AsyncTask<String, Void, String[]> {

            public String[] getTrailerFromJson(String[] trailerQueryJsonStr) throws JSONException {


                // These are the names of the JSON objects that need to be extracted
                //trailer portion
                final String TMB_RESULTS = "results";
                final String TMB_NAME = "name";
                final String TMB_SITE = "site";
                final String TMB_KEY = "key";

                //reviews bit
                final String TMB_AUTHOR = "author";
                final String TMB_CONTENT = "content";

                JSONObject moviesJson = new JSONObject(trailerQueryJsonStr[0]);
                JSONArray moviesArray = moviesJson.getJSONArray(TMB_RESULTS);

                int numMovies = moviesArray.length();
                String[] resultStrs = new String[numMovies];


                for (int i = 0; i < moviesArray.length(); i++) {

                    String name;
                    String site;
                    String key;

                    JSONObject movieDetail = moviesArray.getJSONObject(i);

                    name = movieDetail.getString(TMB_NAME);
                    site = movieDetail.getString(TMB_SITE);
                    key = movieDetail.getString(TMB_KEY);

                    if (site.equals("YouTube")) {
                        resultStrs[i] = name;
                        trailerCode[i] = key;
                        Log.e(LOG_TAG, key);
                    }

                }
                //review portion
                JSONObject reviewsJson = new JSONObject(trailerQueryJsonStr[1]);
                JSONArray reviewsArray = reviewsJson.getJSONArray(TMB_RESULTS);

                //int numReviews = reviewsArray.length();
                //String[] resultStrs = new String[numReviews];

                reviews.clear();
                for (int i = 0; i < reviewsArray.length(); i++) {

                    String author;
                    String content;

                    JSONObject reviewsDetail = reviewsArray.getJSONObject(i);

                    author = reviewsDetail.getString(TMB_AUTHOR);
                    content = reviewsDetail.getString(TMB_CONTENT);

                        reviews.add("»" + author + ": " + content);
                        Log.e(LOG_TAG, reviews.get(i));

                }
                //making sure we do or dont have reviews (testing)
                reviewLength = reviews.size();

                Log.e("@@@review count pls", String.valueOf(reviewLength));
                return resultStrs;
            }

            @Override
            protected String[] doInBackground(String... params) {

                //carried over api and ID from last screen's click
                Intent intent = getActivity().getIntent();
                Bundle extras = intent.getExtras();

                String API_KEY = intent.getStringExtra("API_KEY");
                String Movie_ID = intent.getStringExtra("MOVIE_ID");

                if (params.length == 0) {

                    //don't do anything yet, may not be useful
                }

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String postersJsonStr[] = new String[2];
                try {
                    final String TMDB_BASE_URL =
                            "http://api.themoviedb.org/3/movie/" + Movie_ID + "/videos?&api_key=" + API_KEY;

                    URL url = new URL(TMDB_BASE_URL.toString());


                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    postersJsonStr[0] = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                //try and get reviews at the same time...
                //http://api.themoviedb.org/3/movie/49026/reviews?&api_key=

                try {
                    final String TMDB_BASE_URL =
                            "http://api.themoviedb.org/3/movie/" + Movie_ID + "/reviews?&api_key=" + API_KEY;

                    URL url = new URL(TMDB_BASE_URL.toString());


                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    postersJsonStr[1] = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getTrailerFromJson(postersJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }
            @Override
            protected void onPostExecute(String[] result) {
                Log.e("onPostExe", "is running???");
                if (result != null){
                    mTrailerAdapter.clear();
                    mReviewAdapter.clear();
                    mReviewAdapter.notifyDataSetChanged();
                    // checking for the initial dummy data
                    //if(falseData = false) {
                        for (String trailerName : result) {
                            mTrailerAdapter.add(trailerName);
                            Log.e("Trailer@name", trailerName);
                        }
                        for (int i = 0; i < reviewLength; i++) {
                            mReviewAdapter.add(reviews.get(i));
                            Log.e("Review@name", reviews.get(i));                        }
                    //}
                }
            }
        }
    }
}