package com.example.musicapp;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobItem;
import datab.DataBase;
import datab.MusicDB;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Metadata;
import model.MetadataExtractor;
import service.UserSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class MusicController {
    private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net";
    private static final String CONTAINER_NAME = "media-files";
    public Button refreshUserLibButton;
    @FXML
    private TableView<Metadata> userLib;
    private DataBase database = new DataBase(); // Database instance

    @FXML
    private TableColumn<Metadata, String> userLibSongNameColumn;

    @FXML
    private TableColumn<Metadata, String> userLibArtistColumn;

    @FXML
    private TableColumn<Metadata, String> userLibDurationColumn;

    @FXML
    private TableView<Metadata> metadataTable;
    @FXML
    private TableColumn<Metadata, Void> actionColumn;


    @FXML
    private TableColumn<Metadata, String> songNameColumn;

    @FXML
    private TableColumn<Metadata, String> artistColumn;

    @FXML
    private TableColumn<Metadata, String> durationColumn;

    @FXML
    private TableColumn<Metadata, String> albumColumn;

    @FXML
    private TableColumn<Metadata, String> genreColumn;


    private MusicDB musicBlobDB;
    MusicDB store = new MusicDB();
    @FXML
    public ListView<String> currentPlaylist;
    @FXML
    private ImageView albumArt, profileImage;

    @FXML
    private ImageView profilePic; // next to the username should display a pic of the user and should be allowed to change it
    @FXML
    private Label songTitle, usernameLabel, firstNameLabel, lastNameLabel, addressLabel;
    @FXML
    private Button playButton, pauseButton, nextButton, previousButton, themeToggleButton, paymentButton;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane overlayPane;
    @FXML
    private BorderPane profilePane;

    @FXML
    private Text nameId;
    private String userEmail;


    private boolean isDarkMode = false;
    private MediaPlayer mediaPlayer;

    // Variables for tracking drag offsets
    private double xOffset = 0;
    private double yOffset = 0;


    public void initialize() {
        // Retrieve user session details
        UserSession session = UserSession.getInstance();

        // Use session details to load user-specific data
        int userId = session.getUserId();
        String email = session.getEmail();
        String fullName = session.getUserName();


        // Display user information
        usernameLabel.setText(email);
        nameId.setText(fullName);

        // Load user-specific data (e.g., library, playlists)
        loadUserLibrary(userId);
        initializeMediaPlayer();
        makeProfilePaneDraggable();
        musicBlobDB = new MusicDB();
        // Set up columns for user library
        userLibSongNameColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
        userLibArtistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        userLibDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));


        // Set up property value factories for other columns
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        durationColumn.setCellFactory(new Callback<TableColumn<Metadata, String>, TableCell<Metadata, String>>() {
            @Override
            public TableCell<Metadata, String> call(TableColumn<Metadata, String> param) {
                return new TableCell<Metadata, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            // Convert string to integer and format as "Xm Ys"
                            try {
                                int seconds = Integer.parseInt(item);
                                int minutes = seconds / 60;
                                int remainingSeconds = seconds % 60;
                                setText(minutes + "m " + remainingSeconds + "s");
                            } catch (NumberFormatException e) {
                                setText("Invalid duration"); // Handle invalid data gracefully
                            }
                        }
                    }
                };
            }
        });

        // Load metadata into the table
        loadMetadataIntoTable();
        addButtonToTable();
    }




    private void loadMetadataIntoTable() {
        ObservableList<Metadata> metadataList = FXCollections.observableArrayList();

        // Fetch metadata from MusicDB
        for (BlobItem blobItem : musicBlobDB.getContainerClient().listBlobs()) {
            try {
                // Create a BlobClient for the current blob
                BlobClient blobClient = musicBlobDB.getContainerClient().getBlobClient(blobItem.getName());

                // Use MetadataExtractor to extract metadata from the blob
                Metadata metadata = MetadataExtractor.extractMetadataDB(blobClient, blobItem.getName());

                // Add metadata to the list
                metadataList.add(metadata);
            } catch (Exception e) {
                // Log the error and continue processing other blobs
                System.err.println("Error fetching metadata for blob: " + blobItem.getName() + " - " + e.getMessage());
            }
        }

        // Set the items for the table
        metadataTable.setItems(metadataList);

        // Show placeholder if the table is empty
        if (metadataList.isEmpty()) {
            metadataTable.setPlaceholder(new Label("No metadata available."));
        }
    }
    private void loadUserLibrary(int userId) {
        // Fetch the user's library from the database
        ObservableList<Metadata> library = FXCollections.observableArrayList(database.getUserLibrary(userId));

        // Populate the userLib TableView
        userLib.setItems(library);

        // Show placeholder if the table is empty
        if (library.isEmpty()) {
            userLib.setPlaceholder(new Label("Your library is empty."));
        }
    }



    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add to Library");

            {
                addButton.setOnAction(event -> {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId(); // Fetch user ID from session

                    // Add the song to the user's library

                    database.addToUserSongs(userId, metadata);

                    // Disable the button after adding
                    addButton.setDisable(true);

                    // Refresh the user library
                    loadUserLibrary(userId);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Clear the button if no data
                } else {
                    Metadata metadata = getTableView().getItems().get(getIndex());
                    int userId = UserSession.getInstance().getUserId();

                    // Check if the song is already in the user's library
                    if (database.isSongInUserLibrary(userId, metadata.getBlobName())) {
                        addButton.setDisable(true); // Disable if already in library
                    } else {
                        addButton.setDisable(false); // Enable otherwise
                    }
                    setGraphic(addButton);
                }
            }
        });
    }



    // Method to refresh and display metadata in the ListView
    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Reload metadata into the table
                Platform.runLater(() -> {
                    loadMetadataIntoTable();
                });
                return null;
            }
        };
        new Thread(task).start();
    }





    private void makeProfilePaneDraggable() {
        profilePane.setOnMousePressed(event -> {
            // Capture the initial offset when mouse is pressed
            xOffset = event.getSceneX() - profilePane.getTranslateX();
            yOffset = event.getSceneY() - profilePane.getTranslateY();
        });

        profilePane.setOnMouseDragged(event -> {
            // Adjust translateX and translateY based on current mouse position and offset
            profilePane.setTranslateX(event.getSceneX() - xOffset);
            profilePane.setTranslateY(event.getSceneY() - yOffset);
        });
    }
    public void initializeMediaPlayer() {
        URL resource = getClass().getResource("/DemoSong.mp3");
        URL imageUrl = getClass().getResource("/DefaultAlbumCoverArt.jpg");
        if (imageUrl != null) {
            albumArt.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.out.println("Image file not found at /DefaultAlbumCoverArt.jpg");
        }

        if (resource != null) {
            Media media = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(media);
        } else {
            System.out.println("MP3 file not found!");
        }
    }

    @FXML
    public void handleProfileAction(ActionEvent actionEvent) {
        overlayPane.setVisible(true);
        URL profileImageUrl = getClass().getResource("/ProfileImage.jpg");
        if (profileImageUrl != null) {
            profileImage.setImage(new Image(profileImageUrl.toExternalForm()));
        }
        usernameLabel.setText("johndoe123");
        firstNameLabel.setText("John");
        lastNameLabel.setText("Doe");
        addressLabel.setText("123 Main St, Anytown, USA");
    }

    @FXML
    public void closeProfilePane() {
        overlayPane.setVisible(false);
    }


    public void onPlayButtonClick() {
        if (mediaPlayer != null) {
            System.out.println("Playing audio...");
            mediaPlayer.play();
        }
    }

    public void onPauseButtonClick() {
        if (mediaPlayer != null) {
            System.out.println("Pausing audio...");
            mediaPlayer.pause();
        }
    }

    @FXML
    protected void onNextButtonClick() {
        songTitle.setText("Next Song");
    }

    @FXML
    protected void onPreviousButtonClick() {
        songTitle.setText("Previous Song");
    }

    //toggle for light and dark themes
    @FXML
    protected void onThemeToggleButtonClick() {
        // Get the current scene
        Scene scene = rootPane.getScene();

        // Check if the current theme is dark or light
        if (isDarkMode) {
            System.out.println("LightMode Active");
            // Remove the dark theme and add the light theme
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(getClass().getResource("/com/example/musicresources/light-theme.css").toExternalForm()); // Add light theme
        } else {
            // Remove the light theme and add the dark theme
            System.out.println("DarkMode Active");
            scene.getStylesheets().clear(); // Clear existing stylesheets
            scene.getStylesheets().add(getClass().getResource("/com/example/musicresources/dark-theme.css").toExternalForm()); // Add dark theme
        }

        // Toggle the theme mode flag
        isDarkMode = !isDarkMode;
    }



    public void handlePreferencesAction(ActionEvent actionEvent) {
    }

    public void handleHelpAction(ActionEvent actionEvent) {
    }


    public void handlePaymentAction(ActionEvent actionEvent) {
    }

    public void handleSetting_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/settings.fxml"));
        Parent root = loader.load();

//        // Get the controller from the FXMLLoader
//        MusicController musicController = loader.getController();
//        musicController.setUserName(fullName); // Set the full name in the controller

        // Launch the MusicApplication
        Stage settingsStage = new Stage();
        settingsStage.setScene(new Scene(root));
        settingsStage.show();

    }

    public void handleLikes_btn(ActionEvent event) {
    }

    public void handlePlayList_btn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/playlist-view.fxml"));
        Parent root = loader.load();

//        // Get the controller from the FXMLLoader
//        MusicController musicController = loader.getController();
//        musicController.setUserName(fullName); // Set the full name in the controller

        // Launch the MusicApplication
        Stage playListStage = new Stage();
        playListStage.setScene(new Scene(root));
        playListStage.show();
    }

    public void handleLibrary_btn(ActionEvent event) {
    }

    public void handleSearch_btn(ActionEvent event) {
    }

    public void handleHome_btn(ActionEvent event) {
    }

    //should bring user back to the loin in screen
    public void handleLogOutAction(ActionEvent event) {

        try {
            // Load the login screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/musicresources/login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage (window) from the button or any other control in the scene
            Stage currentStage = (Stage) rootPane.getScene().getWindow();

            // Set the new scene (login screen) on the current stage
            currentStage.setScene(new Scene(loginRoot));

            // Optional: Add title for the login screen
            currentStage.setTitle("Login");

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void handleUpload_btn(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.mp4", "*.wav", "*.aac", "*.flac", "*.ogg", "*.wma", "*.m4a")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Progress dialog setup
            ProgressBar progressBar = new ProgressBar(0);
            Label statusLabel = new Label("Uploading...");
            VBox vbox = new VBox(statusLabel, progressBar);
            vbox.setSpacing(10);
            vbox.setAlignment(Pos.CENTER);
            Stage progressStage = new Stage();
            progressStage.setScene(new Scene(vbox, 300, 100));
            progressStage.setTitle("Upload Progress");
            progressStage.initModality(Modality.APPLICATION_MODAL);
            progressStage.show();

            Task<Void> uploadTask = createUploadTask(selectedFile);
            progressBar.progressProperty().bind(uploadTask.progressProperty());
            statusLabel.textProperty().bind(uploadTask.messageProperty());

            // Handle task success or failure
            uploadTask.setOnSucceeded(event -> {
                progressStage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "File uploaded successfully!", ButtonType.OK);
                alert.showAndWait();
            });

            uploadTask.setOnFailed(event -> {
                progressStage.close();
                Alert alert = new Alert(Alert.AlertType.ERROR, "File upload failed: " + uploadTask.getException().getMessage(), ButtonType.OK);
                alert.showAndWait();
                uploadTask.getException().printStackTrace(); // Log the exact error
            });

            // Start the upload task
            new Thread(uploadTask).start();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No file selected!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private Task<Void> createUploadTask(File file) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                MusicDB musicDB = new MusicDB();
                BlobClient blobClient = musicDB.getContainerClient().getBlobClient(file.getName());

                // Extract metadata
                Map<String, String> metadata = MetadataExtractor.extractMetadata(file);

                // Upload file to Azure Blob Storage
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                updateMessage("Starting upload...");

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024];
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;
                        updateProgress(uploadedBytes, fileSize);
                        updateMessage(String.format("Uploading... %.2f%%", (uploadedBytes / (double) fileSize) * 100));
                    }
                }

                // Set metadata after successful upload
                blobClient.setMetadata(metadata);
                System.out.println("Metadata set for blob: " + file.getName());

                updateMessage("Upload complete");
                return null;
            }
        };
    }

    private void saveMetadataToDatabase(Map<String, String> metadata, String blobName, String userId) {
        try (Connection conn = DriverManager.getConnection(DataBase.DB_URL, DataBase.USERNAME, DataBase.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserSongs (user_id, blob_name, title, duration, artist, album, composer, year_recorded) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, userId);
            stmt.setString(2, blobName);
            stmt.setString(3, metadata.getOrDefault("title", "Unknown Title"));
            stmt.setString(4, metadata.getOrDefault("duration", "0"));
            stmt.setString(5, metadata.getOrDefault("artist", "Unknown Artist"));
            stmt.setString(6, metadata.getOrDefault("album", "Unknown Album"));
            stmt.setString(7, metadata.getOrDefault("composer", "Unknown Composer"));
            stmt.setString(8, metadata.getOrDefault("year", "0"));

            stmt.executeUpdate();
            System.out.println("Metadata saved to database successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to save metadata to database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRefreshUserLibrary(ActionEvent event) {
        // Logic to refresh the user library
        int userId = UserSession.getInstance().getUserId();
        ObservableList<Metadata> library = FXCollections.observableArrayList(database.getUserLibrary(userId));
        userLib.setItems(library);
    }
}