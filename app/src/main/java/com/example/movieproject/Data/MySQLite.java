package com.example.movieproject.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.example.movieproject.RecycleViewPackage.Movie;
import java.util.ArrayList;
import java.util.List;

public class MySQLite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "moviedatabase.db";
    public static final String ITEMS_TABLE = "ITEMS_TABLE";
    public static final String COLUMN_TITLE = "ITEM_TITLE";
    public static final String COLUMN_IMAGE = "ITEM_IMAGE";
    public static final String COLUMN_RATING = "ITEM_RATING";
    public static final String COLUMN_RELEASE_YEAR = "ITEM_RELEASE_YEAR";
    public static final String COLUMN_GENRE = "ITEM_GENRE";


    public MySQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    //Creating database
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + ITEMS_TABLE
                + " (" + COLUMN_TITLE + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_RATING + " TEXT, "
                + COLUMN_RELEASE_YEAR + " TEXT, "
                + COLUMN_GENRE + " TEXT )";

        db.execSQL(createTableStatement);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    //Save items to database
    public boolean addItem(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, movie.getTitle());
        cv.put(COLUMN_IMAGE, movie.getImage());
        cv.put(COLUMN_RATING, movie.getRating());
        cv.put(COLUMN_RELEASE_YEAR, movie.getReleaseYear());
        cv.put(COLUMN_GENRE, movie.getGenre());

        long insert = db.insert(ITEMS_TABLE, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }


    //Returning all items from database
    public List<Movie> getMovieList() {
        List<Movie> movieList = new ArrayList<>();

        String queryString = "SELECT * FROM " + ITEMS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String itemTitle = cursor.getString(0);
                String itemImage = cursor.getString(1);
                String itemRating = cursor.getString(2);
                String itemReleaseYear = cursor.getString(3);
                String itemgenre = cursor.getString(4);

                Movie movie = new Movie(itemTitle, itemImage, itemRating, itemReleaseYear, itemgenre);
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return movieList;
    }


    //Checking if movie exist in database
    public boolean movieExistInDatabase(Movie movie) {

        for (int i = 0; i < getMovieList().size(); i += 1) {
            if (getMovieList().get(i).getTitle().equals(movie.getTitle())) {
                return true;
            } else if (i + 1 == getMovieList().size()) {
                return false;
            }
        }
        return false;
    }
















}
