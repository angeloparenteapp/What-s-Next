package com.angeloparenteapp.upcomingmovies.MyClasses;

public class Cast {

    String character;
    String actorName;
    String actorPhoto;

    public Cast(String character, String actorName, String actorPhoto) {
        this.character = character;
        this.actorName = actorName;
        this.actorPhoto = actorPhoto;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setActorPhoto(String actorPhoto) {
        this.actorPhoto = actorPhoto;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getActorName() {
        return actorName;
    }

    public String getActorPhoto() {
        return actorPhoto;
    }

    public String getCharacter() {
        return character;
    }
}
