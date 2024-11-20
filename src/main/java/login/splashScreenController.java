//package login;
//
//import javafx.animation.PauseTransition;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//
////this is the starting part of the app
//
//public class splashScreenController extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file for the splash screen
//        Parent root = FXMLLoader.load(getClass().getResource("/com/example/musicresources/splashScreen.fxml"));
//
//        // Set up the scene
//        Scene scene = new Scene(root, 600, 400);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Music App Splash Screen");
//        primaryStage.show();
//
//        // Pause for 3 seconds, then transition to the main application
//        PauseTransition pause = new PauseTransition(Duration.seconds(3));
//        pause.setOnFinished(event -> {
//            // Load the main application
//            try {
//                login mainApp = new login();
//                mainApp.start(new Stage());
//                primaryStage.close(); // Close splash screen
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        pause.play();
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
