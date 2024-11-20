package login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class register {

    public void start(Stage primaryStage) throws Exception {


        //try user Id = s@gmail.com
        //pass = 123
        // Load FXML for the registration screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/registeration.fxml"));
        Parent root = loader.load();

        // Set up the primary stage
        primaryStage.setTitle("Music App Registration");

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(root, 540, 480));
        primaryStage.show();
        // Set fixed width and height
//        primaryStage.setScene(new Scene(root, 400, 370));
//
//        // Disable resizing
//        primaryStage.setResizable(false);
//
//        primaryStage.show();

        // Get the controller and add a listener to handle successful registration
        registerationController controller = loader.getController();
        controller.setOnRegistrationSuccess(() -> showSuccessAlert());
    }

    // Method to display a success alert after registration
    private void showSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText(null);
        alert.setContentText("You have registered successfully!");
        alert.showAndWait();
    }
}
