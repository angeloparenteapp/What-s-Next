package com.angeloparenteapp.upcomingmovies.MyClasses;

public class MainPoster {

    private String posterTitle;
    private String posterImage;
    private String posterOverview;
    private String backdropPath;
    private int posterId;
    private boolean isMovie;
    private String releaseDate;

    public MainPoster(String posterTitle,
                      String posterImage,
                      String backdropPath,
                      String posterOverview,
                      int posterId,
                      boolean isMovie,
                      String releaseDate) {

        this.posterTitle = posterTitle;
        this.posterImage = posterImage;
        this.posterOverview = posterOverview;
        this.backdropPath = backdropPath;
        this.posterId = posterId;
        this.isMovie = isMovie;
        this.releaseDate = releaseDate;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public void setPosterTitle(String posterTitle) {
        this.posterTitle = posterTitle;
    }

    public void setPosterOverview(String posterOverview) {
        this.posterOverview = posterOverview;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setPosterId(int posterId) {
        this.posterId = posterId;
    }

    public void setMovie(boolean movie) {
        isMovie = movie;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public String getPosterTitle() {
        return posterTitle;
    }

    public String getPosterOverview() {
        return posterOverview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public int getPosterId() {
        return posterId;
    }

    public boolean isMovie() {
        return isMovie;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
