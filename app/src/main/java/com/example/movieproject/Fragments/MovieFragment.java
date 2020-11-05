package com.example.movieproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.movieproject.R;
import com.example.movieproject.RecycleViewPackage.Movie;
import org.json.JSONArray;

public class MovieFragment extends Fragment {

    private static final String TAG = "MovieDetailsFragment";
    private Context context;
    private ImageView imageView;
    private Intent intent;
    private TextView name, year, rating, genre, rating2, genre2;

    public MovieFragment(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movie, container, false);

        imageView = view.findViewById(R.id.fragMovieIV);
        name = view.findViewById(R.id.fragMovieTV_title);
        year = view.findViewById(R.id.fragMovieTV_releaseYear2);
        rating = view.findViewById(R.id.fragMovieTV_rating1);
        rating2 = view.findViewById(R.id.fragMovieTV_rating2);
        genre = view.findViewById(R.id.fragMovieTV_genre1);
        genre2 = view.findViewById(R.id.fragMovieTV_genre2);
        setPointer();

        return view;
    }


    protected void setPointer() {
        Movie movie = new Movie();

        movie.setImage(intent.getStringExtra("image"));
        movie.setTitle(intent.getStringExtra("title"));
        movie.setReleaseYear(intent.getStringExtra("year"));
        movie.setRating(intent.getStringExtra("rating"));

        rating.setVisibility(View.VISIBLE);
        rating2.setVisibility(View.VISIBLE);
        genre.setVisibility(View.VISIBLE);
        genre2.setVisibility(View.VISIBLE);

        //Converting 'JSON genre' to UI genre
        String genreString = "(";
        try {
            JSONArray genreArray = new JSONArray(intent.getStringExtra("genre"));
            for (int i = 0; i < genreArray.length(); i += 1) {
                genreString = genreString + " " + genreArray.get(i) + ",";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e);
        }
        movie.setGenre(genreString.substring(0, genreString.length() - 1) + ")");


        Glide.with(context)
                .load(movie.getImage())
                .placeholder(R.drawable.ic_image_search)
                .into(imageView);

        name.setText(movie.getTitle());
        year.setText(movie.getReleaseYear());
        rating2.setText(movie.getRating());
        genre2.setText(movie.getGenre());
    }
}
