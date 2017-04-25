package com.example.eng_mayar.popularmoviesapp.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

import static com.example.eng_mayar.popularmoviesapp.contentprovider.FavouriteMovieContract.FavouriteMoviesTable.TABLE_NAME;


public class FavouriteMovieProvider extends ContentProvider {
    private static UriMatcher uriMatcher = buildUriMatcher();
    private static HashMap<String, String> movieMap;
    private FavouriteMovieDbHelper mHelper;
    private SQLiteDatabase db;
    static final int MOVIES = 1;                // request all movies in the DB
    static final int MOVIES_ID = 2;             // request a specific movie according to it's _ID

    //FavouriteMovies._ID = ?
    private static final String idSelection =
            FavouriteMovieContract.FavouriteMoviesTable.TABLE_NAME +
                    "." + FavouriteMovieContract.FavouriteMoviesTable._ID + " = ? ";

    static UriMatcher buildUriMatcher() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteMovieContract.CONTENT_AUTHORITY, "movies", MOVIES);
        uriMatcher.addURI(FavouriteMovieContract.CONTENT_AUTHORITY, "movies/#", MOVIES_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new FavouriteMovieDbHelper(getContext());
        db = mHelper.getWritableDatabase();
        return (db != null);
    }

    private Cursor getMovieByID(Uri uri, String[] projection, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        int idNum = FavouriteMovieContract.FavouriteMoviesTable.getIDFromUri(uri);

        String selection = selection = idSelection;
        String[] selectionArgs = selectionArgs = new String[]{Integer.toString(idNum)};

        return queryBuilder.query(mHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            //>  movies
            case MOVIES:
                cursor = mHelper.getReadableDatabase().query(
                        FavouriteMovieContract.FavouriteMoviesTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            //>  movies/#
            case MOVIES_ID:
                cursor = getMovieByID(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            //>  movies
            case MOVIES:
                return FavouriteMovieContract.FavouriteMoviesTable.CONTENT_TYPE;

            //>  movies/#
            case MOVIES_ID:
                return FavouriteMovieContract.FavouriteMoviesTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) throws android.database.SQLException{
        long row = db.insert(TABLE_NAME, null, values);
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(FavouriteMovieContract.CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new android.database.SQLException("Failed to add a new record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                // delete all the records in the table
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                String id = uri.getLastPathSegment();    //gets the id
                count = db.delete(TABLE_NAME, FavouriteMovieContract.FavouriteMoviesTable._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIES_ID:
                count = db.update(TABLE_NAME, values, FavouriteMovieContract.FavouriteMoviesTable._ID + " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}