//package com.example.musicapp;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class MusicApplication extends Application {
//
//    private String userName; // Store the logged-in user's name
//
//    public MusicApplication(String userName) {
//        this.userName = userName;
//    }
//
//    public void start(Stage stage) throws IOException {
//        // Adjust the path to the FXML file based on its actual location in your project
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
//
//        // Load the FXML and create the scene
//        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
//
//        // Apply the stylesheet
//        scene.getStylesheets().add(getClass().getResource("/com/example/musicresources/light-theme.css").toExternalForm());
//
//        // Set the stage properties
//        stage.setTitle("Music App");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//}
