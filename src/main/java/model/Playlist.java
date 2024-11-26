package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private int id;
    private String name;
    private ObservableList<Song> songs;


    // No-argument constructor
    public Playlist() {
        this.id = 0; // Default ID
        this.name = ""; // Default name
        this.songs = FXCollections.observableArrayList(); // Initialize the list of songs
    }
    // Constructor
    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.songs = FXCollections.observableArrayList(); // Initialize the list of songs
    }

    // Getter for playlist ID
    public int getId() {
        return id;
    }

    // Getter for playlist name
    public String getName() {
        return name;
    }

    // Setter for playlist name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for songs in the playlist
    public ObservableList<Song> getSongs() {
        return songs;
    }

    // Method to add a song to the playlist
    public void addSong(Song song) {
        songs.add(song);
    }

    // Method to remove a song from the playlist
    public void removeSong(Song song) {
        songs.remove(song);
    }
}
