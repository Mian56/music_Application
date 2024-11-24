package login;

import datab.DataBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class registerationController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Text statusMessage;

    private DataBase database;
    private Runnable onRegistrationSuccess;  // Callback for successful registration
    private String localStoragePath;         // Selected storage location


    @FXML
    public void initialize() {
        // Initialize database and connect
        database = new DataBase();
        database.connectToDatabase();
    }

    // Setter for the success callback
    public void setOnRegistrationSuccess(Runnable onRegistrationSuccess) {
        this.onRegistrationSuccess = onRegistrationSuccess;
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation checks
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusMessage.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusMessage.setText("Passwords do not match.");
            return;
        }

        if (localStoragePath == null || localStoragePath.isEmpty()) {
            statusMessage.setText("Please select a storage location.");
            return;
        }

        // Attempt to register the user
        boolean registrationSuccess = database.registerUser(firstName, lastName, email, password, localStoragePath);
        if (registrationSuccess) {
            statusMessage.setText("Registration successful!");

            // Clear fields after successful registration
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            localStoragePath = null;

            // Trigger the success callback if available
            if (onRegistrationSuccess != null) {
                onRegistrationSuccess.run();
            }
        } else {
            statusMessage.setText("Registration failed. Email may already be in use.");
        }
    }

    public void handleBackToLogin(ActionEvent event) {
        try {
            // Load the login FXML file
            login log  = new login();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/login.fxml"));
            Parent loginRoot = loader.load();

//            // Get the current stage (window) and set the login scene
//            Stage stage = (Stage) statusMessage.getScene().getWindow();
//            stage.setScene(new Scene(loginRoot));
//            stage.setTitle("Music App Login");

            // Get the current stage (registration window) and close it
            Stage stage = (Stage) statusMessage.getScene().getWindow();
            stage.close();


        } catch (Exception e) {
            e.printStackTrace();
            statusMessage.setText("Error loading login page.");
        }
    }

    public void pickStorageLocation(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Storage Location");

        Stage stage = (Stage) statusMessage.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            localStoragePath = selectedDirectory.getAbsolutePath();
            statusMessage.setText("Selected storage location: " + localStoragePath);
        } else {
            statusMessage.setText("No directory selected.");
        }
    }

}


