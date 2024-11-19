package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class SearchBar {
    private ObservableList<String> songLibrary;

    public SearchBar() {
        songLibrary = FXCollections.observableArrayList(
                "Song A", "Song B", "Song C", "Song D", "Another Song"
        );
    }
    public ObservableList<String> searchSongs(String query) {
        if (query == null || query.isEmpty()) {
            return songLibrary;
        }
        List<String> filteredSongs = songLibrary.stream()
                .filter(song -> song.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(filteredSongs);
    }
}
