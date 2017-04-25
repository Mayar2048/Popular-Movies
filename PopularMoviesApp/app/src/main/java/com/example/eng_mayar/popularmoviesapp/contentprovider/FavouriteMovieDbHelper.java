package com.example.eng_mayar.popularmoviesapp.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteMovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public FavouriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + FavouriteMovieContract.FavouriteMoviesTable.TABLE_NAME + " (" +
                FavouriteMovieContract.FavouriteMoviesTable._ID + " INTEGER PRIMARY KEY," +
                FavouriteMovieContract.FavouriteMoviesTable.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                FavouriteMovieContract.FavouriteMoviesTable.COLUMN_TITLE + " TEXT NOT NULL," +
                FavouriteMovieContract.FavouriteMoviesTable.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                FavouriteMovieContract.FavouriteMoviesTable.COLUMN_RELEASE_DATE + " REAL NOT NULL," +
                FavouriteMovieContract.FavouriteMoviesTable.COLUMN_OVERVIEW + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieContract.FavouriteMoviesTable.TABLE_NAME);
        onCreate(db);
    }
}
