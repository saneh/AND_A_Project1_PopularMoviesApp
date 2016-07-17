package in.lemonco.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class HomeScreenActivity extends AppCompatActivity {

    private static final String API_KEY = "INSERT_API_KEY";
    private static final String LOG_TAG = HomeScreenActivity.class.getSimpleName();
    private CustomGridViewAdapter mMovieAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayList<Movie> movies = new ArrayList<>();
        mMovieAdapter = new CustomGridViewAdapter(this,movies);
        GridView gridView = (GridView)findViewById(R.id.movie_grid);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.MOVIE_OBJECT,movies.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //fetches the data from movie api
    private void updateMovies() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = pref.getString(getResources().getString(R.string.pref_sort_key),getResources().getString(R.string.sort_order_default));
        new FetchMovieData().execute(sortOrder);
    }

    private class FetchMovieData extends AsyncTask<String, Void, ArrayList<Movie>> {
        private ConnectivityManager mConnectivityManager;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Check network connection.
            if (isNetworkConnected() == false) {
                // Cancel request.
                Log.i(getClass().getName(), "Not connected to the internet");
                Toast.makeText(getApplicationContext(), "Please Check Internet connectivity", Toast.LENGTH_SHORT).show();
                cancel(true);
                return;
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... searchQuery) {
            // Stop if cancelled
            if (isCancelled()) {
                return null;
            }
            if (searchQuery == null) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String key = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(searchQuery[0])
                        .appendQueryParameter(key, API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.i(LOG_TAG + "search query", builtUri.toString());

                // Creates a request to TMDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                moviesJsonStr = null;
            } finally{
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
            Log.i("result", moviesJsonStr);
            try
            {
                //returns the movies arrayList
                return getMovieData(moviesJsonStr);
            }catch (JSONException ex){
                Log.i(LOG_TAG, "Error in Json parsing");
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if(movies!=null){
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }
        }

        //get movie data from JsonString
        private ArrayList<Movie> getMovieData(String moviesJsonStr) throws JSONException{
            final String  TAG_RESULTS = "results";
            final String TAG_TITLE = "original_title";
            final String TAG_OVERVIEW = "overview";
            final String TAG_POSTER = "poster_path";
            final String TAG_RATING ="vote_average";
            final String TAG_RELEASEDATE = "release_date";
            ArrayList<Movie> movies = new ArrayList<>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(TAG_RESULTS);
            Movie movie_object;
            for(int i=0; i<movieArray.length();i++){
                JSONObject movieJsonObj = movieArray.getJSONObject(i);
                String title = movieJsonObj.getString(TAG_TITLE);
                String overview = movieJsonObj.getString(TAG_OVERVIEW);
                String poster = movieJsonObj.getString(TAG_POSTER);
                String rating = movieJsonObj.getString(TAG_RATING);
                String releaseDate = movieJsonObj.getString(TAG_RELEASEDATE);
                movie_object = new Movie(title,overview,rating,releaseDate,poster);
                movies.add(movie_object);
            }
            return movies;

        }
        protected boolean isNetworkConnected() {

            // Instantiate mConnectivityManager if necessary
            if (mConnectivityManager == null) {
                mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            // Is device connected to the Internet?
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
