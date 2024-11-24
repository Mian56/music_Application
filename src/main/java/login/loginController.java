package login;

import com.example.musicapp.MusicController;
import datab.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.UserSession;

import java.io.IOException;

public class loginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text statusMessage;

    // Instance of the Database class to check credentials
    private DataBase database = new DataBase();
    MusicController mC = new MusicController();

    public void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        database.connectToDatabase();

        // Use the database login verification
        if (database.loginUser(email, password)) {
            statusMessage.setText("Login successful!");
            System.out.println("Login successful");

            try {
                // Fetch user details from the database
                int userId = database.getUserId(email); // Fetch user ID
                String fullName = database.getUserFullName(email); // Fetch full name
                // Validate fetched data
                if (userId > 0 && fullName != null) {
                    // Store user session data
                    UserSession session = UserSession.getInstance(userId, email, fullName);

                    // Load the MusicApplication FXML file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/music-view.fxml"));
                    Parent root = loader.load();

                    // Get the controller from the FXMLLoader
                    MusicController musicController = loader.getController();

                    // Launch the MusicApplication
                    Stage musicStage = new Stage();
                    musicStage.setScene(new Scene(root));
                    musicStage.show();

                    // Close the login stage
                    Stage loginStage = (Stage) usernameField.getScene().getWindow();
                    loginStage.close();
                } else {
                    statusMessage.setText("Error fetching user details.");
                    System.out.println("Error fetching user details: userId=" + userId + ", fullName=" + fullName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusMessage.setText("Failed to load Music Application.");
            }
        } else {
            statusMessage.setText("Invalid username or password.");
            System.out.println("Invalid login attempt for email: " + email);
        }
    }


    // Method to open the registration screen
    public void handleRegister() throws Exception {
        register screen = new register();
        screen.start(new Stage());
    }

    public void handleForgotPassword(ActionEvent event) {
        try {
            // Load the forgotPass.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/forgotPass.fxml")); // Replace with your actual FXML file path
            Parent root = loader.load();

            // Create a new stage for the forgot password screen
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally close the current window
             ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace(); // Log the error
        }
    }
}
