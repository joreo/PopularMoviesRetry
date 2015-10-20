package com.udanano.popularmoviesretry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

public class PostersFragment extends Fragment {
    private ImageAdapter mPosterAdapter;

    //****************************************************
    //*************API KEY GOES UNDER HERE****************
    //****************************************************
    final public String API_KEY = "xxxxx";

    //an array of movie objects
    Movies[] movies = new Movies[20];

    public PostersFragment() {
    }

    @Override
    public void onResume(){
        super.onResume();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Log.v("Steps", "Called updateMovies() onCreate");
        updateMovies();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.v("Steps", "Called updateMovies() onOptionsItemSelected");
            updateMovies();
            mPosterAdapter.notifyDataSetChanged(); // doesn't seem to update my movielist w/o a rotate
            return true;
        }
        //return super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    private void updateMovies() {
        FetchMovieInfo movieInfo = new FetchMovieInfo();
        movieInfo.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public class FetchMovieInfo extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieInfo.class.getSimpleName();

        public String[] getMovieDataFromJson(String movieQueryJsonStr) throws JSONException {


            // These are the names of the JSON objects that need to be extracted
            final String TMB_RESULTS = "results";
            final String TMB_POSTER = "poster_path";
            final String TMB_TITLE = "title";
            final String TMB_VOTE_AVG = "vote_average";
            final String TMB_OVERVIEW = "overview";
            final String TMB_RELEASE_DATE = "release_date";
            final String TMB_POPULARITY = "popularity";
            //added for stage 2
            final String TMB_ID = "id";

            JSONObject moviesJson = new JSONObject(movieQueryJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMB_RESULTS);

            int numMovies = moviesArray.length();
            String[] resultStrs = new String[numMovies];



            for (int i = 0; i < moviesArray.length(); i++) {

                String poster;
                String title;
                String average;
                String overview;
                String release_date;
                String popularity;
                String id;

                JSONObject movieDetail = moviesArray.getJSONObject(i);

                poster = movieDetail.getString(TMB_POSTER);
                title = movieDetail.getString(TMB_TITLE);
                release_date = movieDetail.getString(TMB_RELEASE_DATE);
                average = movieDetail.getString(TMB_VOTE_AVG);
                overview = movieDetail.getString(TMB_OVERVIEW);
                popularity = movieDetail.getString(TMB_POPULARITY);
                id = movieDetail.getString(TMB_ID);

                resultStrs[i] = poster;
                //adding the movie info into our movie objects.
                movies[i] = new Movies (poster, title, average, overview, release_date, popularity, id);

            }


            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {

                //don't do anything yet, may not be useful
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String searchType ="popularity.desc";


            // Will contain the raw JSON response as a string.
            String postersJsonStr = null;
            try {

                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                String unitType = sharedPrefs.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_rating));
                if (unitType.equals("Rating")){
                    searchType = "vote_average.desc";
                } else {
                    searchType = "popularity.desc";
                }

                Log.e("Favorite test", unitType);


                 final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?sort_by=" + searchType + "&api_key=" + API_KEY;

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
                postersJsonStr = buffer.toString();
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
                return getMovieDataFromJson(postersJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String[] result) {

            GridView gridView = (GridView) getView().findViewById(R.id.gridview_posters);

            //sort movie objects, we hope
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_rating));

            //put a try/catch here pls
            try {
            Log.e("Favorite test", unitType);
            if (unitType.equals("Rating")){
                Arrays.sort(movies, Movies.MovieRatingComparator);
                mPosterAdapter = new ImageAdapter(getActivity(), Arrays.asList(movies));
            } else if (unitType.equals("Favorites")) {
                    //should be Favorites, changed temporarily

                DatabaseOps DOP = new DatabaseOps(getActivity());
                Cursor CR = DOP.getEntry(DOP);

                //I don't know how to display an empty adapter
                //so let's show something else
                int cursorCount = CR.getCount();
                if(cursorCount != 0){
                    CR.moveToFirst();
                    List<Movies> favMovies = new ArrayList<Movies>();
                    do{
                     favMovies.add(new Movies(
                            CR.getString(0),
                            CR.getString(1),
                            CR.getString(2),
                            CR.getString(3),
                            CR.getString(4),
                            CR.getString(5),
                            CR.getString(6)));

                 }while(CR.moveToNext());
                    mPosterAdapter = new ImageAdapter(getActivity(), favMovies);
                }else{
                    Toast.makeText(getActivity(), "Favorites are empty, showing Popular movies", Toast.LENGTH_LONG).show();
                    Arrays.sort(movies, Movies.MoviePopularityComparator);
                    mPosterAdapter = new ImageAdapter(getActivity(), Arrays.asList(movies));
                }
                DOP.close();
            } else {
                //sort by pop
                Arrays.sort(movies, Movies.MoviePopularityComparator);
                mPosterAdapter = new ImageAdapter(getActivity(), Arrays.asList(movies));

            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

            gridView.setAdapter(mPosterAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Movies movies = mPosterAdapter.getItem(i);
                    Bundle extras = new Bundle();
                    extras.putString("MOVIE_POSTER", movies.poster);
                    extras.putString("MOVIE_TITLE", movies.title);
                    extras.putString("MOVIE_RATING", movies.average);
                    extras.putString("MOVIE_PLOT", movies.overview);
                    extras.putString("MOVIE_RELEASE_DATE", movies.release_date);
                    extras.putString("API_KEY", API_KEY);
                    extras.putString("MOVIE_ID", movies.id);
                    extras.putString("MOVIE_POP", movies.popularity);
                    Intent intent = new Intent(getActivity(), MovieDetail.class)
                            .putExtras(extras);


                    //if this is tablet view, take over that frame/fragment. dont eat my whole window, bro

                    MovieDetail.DetailFragment displayFrag = (MovieDetail.DetailFragment) getFragmentManager()
                            .findFragmentById(R.id.detail_frag);

                    if (displayFrag == null) {
                        Log.e("v==","null");
                        startActivity(intent);
                    } else {
                        Log.e("v==","not null");
                        // fragment replacement stuff
                        MovieDetail.DetailFragment fragment = new MovieDetail.DetailFragment();
                        fragment.setArguments(extras);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.detail_frag, fragment, "frag tag")
                                .commit();

                    }

            }});

//            if (result != null) {
//                mPosterAdapter.clear();
//                for (String resultStrs : result) {
//                   // mPosterAdapter.add(resultStrs);
//                    // uhhh do nothing I guess! since it works as-is!
//                }
//                mPosterAdapter.notifyDataSetChanged();

                // New data is back from the server.  Hooray!
//            }
        }
    }
}