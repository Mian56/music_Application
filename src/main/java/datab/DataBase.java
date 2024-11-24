package datab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Metadata;

import java.sql.*;

public class DataBase {
    // Database connection constants
    final static String DB_NAME = "MusicApplication";

    //iqbama311server.mysql.database.azure.com
    final static String SQL_SERVER_URL = "jdbc:mysql://bandrowskicsc311server.mysql.database.azure.com";
    public final static String DB_URL = SQL_SERVER_URL + "/" + DB_NAME;
    public final static String USERNAME = "bandrowskiadmin";
    public final static String PASSWORD = "Farmingdale24";

    // Method to connect to the database and initialize tables if not already created
    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false; // Exit if driver is not found
        }

        try {
            // First, connect to MySQL server and create the database if not created
            try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Connect to the specific database
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {

                // Create users table
                String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                        "first_name VARCHAR(200) NOT NULL," +
                        "last_name VARCHAR(200) NOT NULL," +
                        "email VARCHAR(200) NOT NULL UNIQUE," +
                        "password VARCHAR(255) NOT NULL)";
                statement.executeUpdate(createUserTable);

                // Check if the 'local_storage_path' column exists and add if not
                String checkLocalStorageColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'users' " +
                        "AND COLUMN_NAME = 'local_storage_path'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkLocalStorageColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") == 0) {
                        String addLocalStorageColumnQuery = "ALTER TABLE users ADD COLUMN local_storage_path VARCHAR(255)";
                        statement.executeUpdate(addLocalStorageColumnQuery);
                    }
                }

                String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS playlists (" +
                        "playlist_id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(100) NOT NULL," +
                        "user_id INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))";
                statement.executeUpdate(createPlaylistsTable);

                // Create UserSongs table
                String createUserSongsTable = "CREATE TABLE IF NOT EXISTS UserSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "user_id INT NOT NULL," +
                        "blob_name VARCHAR(255) NOT NULL," +
                        "title VARCHAR(200)," +
                        "duration VARCHAR(200)," +
                        "artist VARCHAR(200)," +
                        "album VARCHAR(200)," +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
                statement.executeUpdate(createUserSongsTable);

                // Check and add 'genre' column to UserSongs if not exists
                String checkGenreColumnQuery = "SELECT COUNT(*) AS cnt " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DB_NAME + "' " +
                        "AND TABLE_NAME = 'UserSongs' " +
                        "AND COLUMN_NAME = 'genre'";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkGenreColumnQuery);
                     ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") == 0) {
                        String addGenreColumnQuery = "ALTER TABLE UserSongs ADD COLUMN genre VARCHAR(200)";
                        statement.executeUpdate(addGenreColumnQuery);
                    }
                }

                String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS PlaylistSongs (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "playlist_id INT NOT NULL," +
                        "user_song_id INT NOT NULL," +
                        "FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (user_song_id) REFERENCES UserSongs(id) ON DELETE CASCADE)";
                statement.executeUpdate(createPlaylistSongsTable);

                // Check if any users are already registered
                try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                    if (resultSet.next()) {
                        hasRegisteredUsers = resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }


    // Method to check if a song exists in the UserSongs table
        public boolean checkIfSongExists(String blobName) {
            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement(
                         "SELECT COUNT(*) FROM UserSongs WHERE blob_name = ?")) {
                stmt.setString(1, blobName);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

    public void addToUserSongs(int userId, Metadata metadata) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO UserSongs (user_id, blob_name, title, duration, artist, album) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, userId);
            stmt.setString(2, metadata.getBlobName());
            stmt.setString(3, metadata.getSongName());
            stmt.setString(4, metadata.getDuration()); // Pass duration as String
            stmt.setString(5, metadata.getArtist());
            stmt.setString(6, metadata.getAlbum());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isSongInUserLibrary(int userId, String blobName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT COUNT(*) FROM UserSongs WHERE user_id = ? AND blob_name = ?")) {

            stmt.setInt(1, userId);
            stmt.setString(2, blobName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if the song exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there's an error or no result
    }
    public int getUserId(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Users WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if not found
    }

    public String getUserFullName(String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM Users WHERE email = ?")) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }


    public ObservableList<Metadata> getUserLibrary(int userId) {
        ObservableList<Metadata> library = FXCollections.observableArrayList();
        String query = "SELECT blob_name, title, duration, artist, album, genre FROM UserSongs WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                library.add(new Metadata(
                        rs.getString("blob_name"),
                        rs.getString("title"),
                        rs.getString("duration"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre") // Ensure this matches your Metadata class
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return library;
    }






    // Method for registering a new user
    public boolean registerUser(String firstName, String lastName, String email, String password, String local_Storage_Path) {
        String insertUserSQL = "INSERT INTO users (first_name, last_name, email, password, local_storage_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, local_Storage_Path);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method for validating login credentials
    public boolean loginUser(String email, String password) {
        String selectUserSQL = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(selectUserSQL)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet resultSet = pstmt.executeQuery();
            return resultSet.next(); // Returns true if a user with the given email and password exists

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to change the password for a user
    public boolean changePassword(String email, String newPassword) {
        String updatePasswordSQL = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updatePasswordSQL)) {

            // Set the new password and email for the query
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);

            // Execute the update
            int rowsUpdated = pstmt.executeUpdate();

            // If rowsUpdated is greater than 0, then the password was updated successfully
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean saveStorageLocation(String email, String storagePath) {
        String updateQuery = "UPDATE users SET local_storage_path = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, storagePath);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
