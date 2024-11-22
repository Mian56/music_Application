package login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class SettingsController {

    @FXML
    private CheckBox darkModeCheckbox;

    @FXML
    private CheckBox notificationsCheckbox;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    public void setUserName(String userName) {
        usernameLabel.setText("Welcome, " + userName);
    }

    @FXML
    private void handleSaveSettings() {
        System.out.println("Dark Mode: " + darkModeCheckbox.isSelected());
        System.out.println("Notifications: " + notificationsCheckbox.isSelected());
        System.out.println("Settings Saved");
    }

    @FXML
    private void handleCancelSettings() {
        System.out.println("Cancel Settings");

    }
}
