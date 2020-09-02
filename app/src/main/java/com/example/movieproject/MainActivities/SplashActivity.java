package com.example.movieproject.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieproject.Data.HttpHandler;
import com.example.movieproject.Data.MySQLite;
import com.example.movieproject.R;
import com.example.movieproject.RecycleViewPackage.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        new JSONAsyncTask().execute();
    }


    public class JSONAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private static final String SERVER_URL = "https://api.androidhive.info/json/movies.json";
        private static final String TAG = "JSONAsyncTask";
        private Animation topAnim, botAnim;
        private ImageView imageView;
        private TextView textView;
        private MySQLite mySQLite;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            animationCreation();
            progressBar = findViewById(R.id.splashPB);
            progressBar.setVisibility(View.VISIBLE);
        }

        //Creating simple animation
        private void animationCreation() {
            topAnim = AnimationUtils.loadAnimation(context, R.anim.top_animation);
            topAnim.setDuration(3000);

            botAnim = AnimationUtils.loadAnimation(context, R.anim.bot_animation);
            botAnim.setDuration(3000);

            imageView = findViewById(R.id.splashIV_image);
            textView = findViewById(R.id.splashTV_title);

            textView.setAnimation(topAnim);
            imageView.setAnimation(botAnim);
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            mySQLite = new MySQLite(context);
            List<Movie> movieList = mySQLite.getMovieList();


            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(SERVER_URL);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i += 1) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        //Creating Movie object from jsonArray
                        String title = object.getString("title");
                        String image = object.getString("image");
                        String rating = object.getString("rating");
                        String releaseYear = object.getString("releaseYear");
                        String genre = object.getString("genre");

                        Movie movie = new Movie(title, image, rating, releaseYear, genre);

                        //If program start first time just add movie to SQLite
                        if (movieList.size() == 0) {
                            addMovieToSQLite(movie);
                        }

                        //Check if current movie exist in SQLite
                        if (!mySQLite.movieExistInDatabase(movie)){
                            addMovieToSQLite(movie);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "doInBackground: Exception " + e);
                }
            }
            return null;
        }

        private void addMovieToSQLite(Movie movie) {
            boolean b = new MySQLite(context).addItem(movie);
            if (b) {
                Log.i(TAG, "doInBackground: " + movie.getTitle() + " item added"); //Notify if item added
            } else {
                Log.i(TAG, "doInBackground: error while adding " + movie.getTitle() + " to database"); //Notify if item not added
            }
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.INVISIBLE);
            //After 3 seconds will start 'MovieListActivity'
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MovieListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        }
    }
}