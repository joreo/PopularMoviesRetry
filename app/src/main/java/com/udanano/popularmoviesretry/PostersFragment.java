package com.udanano.popularmoviesretry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

public class PostersFragment extends Fragment {
    private ImageAdapter mPosterAdapter;

    //an array of movie objects
    Movies[] movies = new Movies[20];

    public ArrayList<String> movieList = new ArrayList<String>();

    public PostersFragment() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        updateMovies();
//        Log.v("Steps", "Called updateMovies() onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_posters);

        //i thought with updateMovies(); here, the movieList would be populated, but nope

//        gridView.setAdapter(mPosterAdapter);

        return rootView;
    }



    private void updateMovies() {
        Log.v("Steps", "in the updateMovies method");
        Log.v("@@@ fetchmovie called?:", "if you see this, yes.");
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

                JSONObject movieDetail = moviesArray.getJSONObject(i);

                poster = movieDetail.getString(TMB_POSTER);
                title = movieDetail.getString(TMB_TITLE);
                release_date = movieDetail.getString(TMB_RELEASE_DATE);
                average = movieDetail.getString(TMB_VOTE_AVG);
                overview = movieDetail.getString(TMB_OVERVIEW);

                resultStrs[i] = poster;
                //adding the movie info into our movie objects.
                movies[i] = new Movies (poster, title, average, overview, release_date);

                movieList.add(poster);
            }

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //sort order uses whatever is selected from the prefs.
            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_rating));
//            Log.v(LOG_TAG, unitType.toString());

            String sortMethod = "popularity.desc";
            if (unitType.equals("Rating")){
                sortMethod = "vote_average.desc";
                Log.v("### in the if", unitType.toString());
                Log.v("## in the if", sortMethod);
            } else {
                Log.v("### in the else", unitType.toString());
                Log.v("### in the else", sortMethod);
            }

            // Will contain the raw JSON response as a string.
            String postersJsonStr = null;
            try {
                //I know I can break this down more, but I just want it to work first
                //****************************************************
                //*************API KEY GOES UNDER HERE****************
                //****************************************************
                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?sort_by="+ sortMethod +"&api_key=xxxx";
                Log.v(LOG_TAG, sortMethod);
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
            //just noting my concern that this only works when i code it here and not
            //on create view
            //mPosterAdapter = new ImageAdapter(getActivity(), movies);
            mPosterAdapter = new ImageAdapter(getActivity(), Arrays.asList(movies));

            GridView gridView = (GridView) getView().findViewById(R.id.gridview_posters);
            gridView.setAdapter(mPosterAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //toast test that shows image path
                    //later this will be an object with the info we need
//                    String msg = mPosterAdapter.getItem(i).toString();
//                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    //pseudo code for my new intent
                   // MovieData movieData = mPosterAdapter.getItem(i);
                    Movies movies = mPosterAdapter.getItem(i);
                    Bundle extras = new Bundle();
                    extras.putString("MOVIE_POSTER", movies.poster);
                    extras.putString("MOVIE_TITLE", movies.title);
                    extras.putString("MOVIE_RATING", movies.average);
                    extras.putString("MOVIE_PLOT", movies.overview);
                    extras.putString("MOVIE_RELEASE_DATE", movies.release_date);
                    Intent intent = new Intent(getActivity(), MovieDetail.class)
                            .putExtras(extras);
                    startActivity(intent);
                }

            });

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