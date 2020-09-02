package com.example.movieproject.MainActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieproject.Data.MySQLite;
import com.example.movieproject.R;
import com.example.movieproject.RecycleViewPackage.Movie;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QRCodeReading extends AppCompatActivity {

    private static final String TAG = "QRCodeReading";
    private Context context;
    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private FloatingActionButton fab;
    private Movie newMovie;
    private MySQLite mySQLite;
    private SparseArray<Barcode> qrCode;
    private CoordinatorLayout coordinatorLayout;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reading);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setPointer();
    }

    private void setPointer() {
        context = this;
        surfaceView = findViewById(R.id.qrCodeSV);
        textView = findViewById(R.id.qrCodeTV);
        fab = findViewById(R.id.qrCodeFAB);
        mySQLite = new MySQLite(context);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newMovie != null) {
                    if (!newMovie.getTitle().isEmpty()) {
                        initializeAlertDialog();
                    }
                } else {
                    Toast.makeText(context, R.string.qrcode_focus_on_qrcode, Toast.LENGTH_SHORT).show();
                }
            }
        });

        initializeCamera();
    }

    private void initializeCamera() {
        //Start camera
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception: surfaceCreated() returned: " + e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        readingQRCode();
    }

    private void readingQRCode() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                qrCode = detections.getDetectedItems();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            //Converting QRCode to Movie.class
                            try {
                                wait(600);
                                JSONObject object = new JSONObject(qrCode.valueAt(0).displayValue);
                                String title = object.getString("title");
                                String image = object.getString("image");
                                String rating = object.getString("rating");
                                String releaseYear = object.getString("releaseYear");
                                String genre = object.getString("genre");
                                newMovie = new Movie(title, image, rating, releaseYear, genre);
                                textView.setText(newMovie.getTitle() + " (Movie)");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "Exception synchronized: ", e);
                            }
                        }
                        Log.i(TAG, "Runnable, QRCode find");
                    }
                };

                //After change focus from movie QRCode notify user about it
                if (qrCode.size() != 0) {
                    runnable.run();
                } else {
                    textView.setText(R.string.qrcode_focus_on_qrcode);
                }
            }
        });
    }

    //AlertDialog for display to user data about scanned movie
    private void initializeAlertDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movie, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(view);

        try {
            ImageView imageView = view.findViewById(R.id.fragMovieIV);
            TextView title = view.findViewById(R.id.fragMovieTV_title);
            TextView releaseYear = view.findViewById(R.id.fragMovieTV_releaseYear2);
            Button add = view.findViewById(R.id.fragMovieBTN_add);
            Button close = view.findViewById(R.id.fragMovieBTN_close);

            add.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(newMovie.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_search)
                    .into(imageView);

            alertDialog.show();
            title.setText(newMovie.getTitle());
            releaseYear.setText(newMovie.getReleaseYear());

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addingMovieToDataBase();
                    alertDialog.dismiss();
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }
        alertDialog.show();
    }

    //Check if movie exist in database and if not add it
    private void addingMovieToDataBase() {
        Snackbar snackbar;
        if (mySQLite.movieExistInDatabase(newMovie)) {
            snackbar = Snackbar.make(coordinatorLayout, R.string.qrcode_movie_exist, BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        } else {
            mySQLite.addItem(newMovie);
            snackbar = Snackbar.make(coordinatorLayout, R.string.qrcode_movie_added, BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future longRunningTaskFuture = executorService.submit(runnable);
        longRunningTaskFuture.cancel(true);
        Intent intent = new Intent(QRCodeReading.this, MovieListActivity.class);
        startActivity(intent);
        finish();
    }
}
