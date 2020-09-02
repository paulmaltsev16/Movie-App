package com.example.movieproject.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.movieproject.Data.MySQLite;
import com.example.movieproject.R;
import com.example.movieproject.RecycleViewPackage.Movie;
import com.example.movieproject.RecycleViewPackage.RecycleViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    public static RecycleViewAdapter adapter;
    private FloatingActionButton fab;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setPointer();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionGranted(Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_CODE)) {
                    Intent intent = new Intent(MovieListActivity.this, QRCodeReading.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private void setPointer() {
        context = this;

        //Asking user for storage permission
       permissionGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);

        initializeRecycleView();

    }


    // Function to check and request permission.
    public boolean permissionGranted(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MovieListActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MovieListActivity.this,
                    new String[]{permission},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MovieListActivity.this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MovieListActivity.this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MovieListActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MovieListActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    public void initializeRecycleView() {
        adapter = new RecycleViewAdapter(context, orderMovieList());
        recyclerView = findViewById(R.id.movieListRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private List<Movie> orderMovieList() {
        List<Movie> movieList = new MySQLite(context).getMovieList();
        for (int i = 0; i < movieList.size(); i += 1) {

            for (int j = 0; j < movieList.size(); j += 1) {

                int firstYear = Integer.parseInt(movieList.get(i).getReleaseYear());
                if (firstYear > Integer.parseInt(movieList.get(j).getReleaseYear())) {
                    Collections.swap(movieList, i, j);
                }
            }
        }
        return movieList;
    }
}