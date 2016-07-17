package in.lemonco.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_OBJECT="movie_obj";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //get intent
        Intent intent = getIntent();
        Movie movie = (Movie)intent.getParcelableExtra(MOVIE_OBJECT);

        ImageView moviePoster = (ImageView)findViewById(R.id.movie_poster_thumbnail);
        Picasso.with(getApplicationContext()).load(movie.getmPosterPath()).into(moviePoster);

        TextView movieTitle = (TextView)findViewById(R.id.movie_title);
        movieTitle.setText(movie.getmOriginalTitle());

        TextView movieRating = (TextView)findViewById(R.id.movie_rating);
        movieRating.setText(movie.getmUserRating());

        TextView releaseDate = (TextView)findViewById(R.id.release_date);
        releaseDate.setText(movie.getmReleaseDate());

        TextView overview = (TextView)findViewById(R.id.movie_overview);
        overview.setText(movie.getmOverview());

        setTitle(movie.getmOriginalTitle()); //sets the activity title as Movie name

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //enable the back button in detail activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
