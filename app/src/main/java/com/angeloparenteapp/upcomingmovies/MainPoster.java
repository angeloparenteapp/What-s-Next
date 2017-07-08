package com.angeloparenteapp.upcomingmovies;

public class MainPoster {

    private String posterImage;
    private String posterTitle;

    public MainPoster(String posterTitle, String posterImage){
        this.posterImage = posterImage;
        this.posterTitle = posterTitle;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public void setPosterTitle(String posterTitle) {
        this.posterTitle = posterTitle;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public String getPosterTitle() {
        return posterTitle;
    }
}
