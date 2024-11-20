package model;

import javafx.beans.property.SimpleStringProperty;

public class Metadata {
    private final SimpleStringProperty songName;
    private final SimpleStringProperty artist;
    private final SimpleStringProperty duration;
    private final SimpleStringProperty album;
    private final SimpleStringProperty genre;

    public Metadata(String songName, String artist, String duration, String album, String genre) {
        this.songName = new SimpleStringProperty(songName);
        this.artist = new SimpleStringProperty(artist);
        this.duration = new SimpleStringProperty(duration);
        this.album = new SimpleStringProperty(genre);
        this.genre = new SimpleStringProperty(genre);
    }

    public String getSongName() {
        return songName.get();
    }

    public String getArtist() {
        return artist.get();
    }

    public String getDuration() {
        return duration.get();
    }

    public String getAlbum() {
        return album.get();
    }

    public String getGenre() {
        return genre.get();
    }
}

