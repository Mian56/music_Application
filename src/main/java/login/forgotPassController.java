package login;

import datab.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class forgotPassController {

    // Injected fields from FXML
    @FXML
    private TextField emailField;  // The field for the user's email
    @FXML
    private PasswordField newPasswordField;  // The field for the new password
    @FXML
    private Label statusLabel;  // Label to display status messages

    // Instantiate the DataBase class
    private DataBase database = new DataBase();

    // Initialize method to check if FXML elements are properly injected
    @FXML
    public void initialize() {
        // Make sure the FXML fields are injected correctly
        assert emailField != null : "emailField was not injected!";
        assert newPasswordField != null : "newPasswordField was not injected!";
        assert statusLabel != null : "statusLabel was not injected!";
    }

    // Method to handle the password reset action
    public void handleResetPasswordAction(ActionEvent event) {
        // Get the text values from the input fields
        String email = emailField.getText();
        String newPassword = newPasswordField.getText();

        // Check if both fields are filled
        if (email.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in all the fields.");
            return;  // Exit the method if any field is empty
        }

        // Call the changePassword method to update the password in the database
        boolean isUpdated = database.changePassword(email, newPassword);

        // Display the appropriate status message based on the result
        if (isUpdated) {
            statusLabel.setText("Password updated successfully!");
        } else {
            statusLabel.setText("Failed to update password. Check the email.");
        }
    }


    public void handleBackToLoginAction(ActionEvent event) {
        try {
            // Load the login screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/login.fxml"));
            Parent loginRoot = loader.load();
            System.out.println("back to login");

            // Get the current stage from the ActionEvent
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the login scene on the current stage
            currentStage.setScene(new Scene(loginRoot));

            // Optional: Set the title for the login screen
            currentStage.setTitle("Login");

            // Show the stage (this step ensures the updated scene is displayed)
            currentStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
