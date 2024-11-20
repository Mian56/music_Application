package model;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String fileUrl;

    // Constructor
    public Song(int id, String title, String artist, String album, String fileUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.fileUrl = fileUrl;
    }

    // Getter for song ID
    public int getId() {
        return id;
    }

    // Setter for song ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for artist
    public String getArtist() {
        return artist;
    }

    // Setter for artist
    public void setArtist(String artist) {
        this.artist = artist;
    }

    // Getter for album
    public String getAlbum() {
        return album;
    }

    // Setter for album
    public void setAlbum(String album) {
        this.album = album;
    }

    // Getter for file URL
    public String getFileUrl() {
        return fileUrl;
    }

    // Setter for file URL
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
