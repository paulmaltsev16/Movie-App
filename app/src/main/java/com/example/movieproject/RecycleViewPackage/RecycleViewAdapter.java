package com.example.movieproject.RecycleViewPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.movieproject.Fragments.MovieDetailsActivity;
import com.example.movieproject.R;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private Context context;
    private List <Movie> movieList;

    public RecycleViewAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Movie movie = movieList.get(position);

        if (position%2 == 0){
            holder.parentLayout.setGravity(1);
        }
        Glide.with(context)
                .load(movie.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_image_search)
                .into(holder.imageView);

        holder.name.setText(movie.getTitle()+" ("+movie.getReleaseYear()+")");

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("image",movie.getImage());
                intent.putExtra("title",movie.getTitle());
                intent.putExtra("year",movie.getReleaseYear());
                intent.putExtra("rating",movie.getRating());
                intent.putExtra("genre",movie.getGenre());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout parentLayout;
        private ImageView imageView;
        private TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.itemMovie);
            imageView = itemView.findViewById(R.id.itemImage);
            name = itemView.findViewById(R.id.itemTV_title);
        }
    }
}
