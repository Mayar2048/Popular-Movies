package com.example.eng_mayar.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eng-Mayar on 02-Nov-16.
 */
public class Movie implements Parcelable {
    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private long id;
    private String title;
    private double popularity;
    private long voteCount;
    private boolean video;
    private double voteAverage;

    protected Movie(Parcel in) {
        posterPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        id = in.readLong();
        title = in.readString();
        popularity = in.readDouble();
        voteCount = in.readLong();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = "http://image.tmdb.org/t/p/w185/" + posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeLong(voteCount);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(voteAverage);
    }
}
