package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class login extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use the absolute path to load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Music App Login");
        primaryStage.setScene(new Scene(root, 697, 440));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
