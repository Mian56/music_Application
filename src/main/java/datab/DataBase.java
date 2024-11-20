package datab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBase {
    // Database connection constants
    final static String DB_NAME = "MusicApplication";

    //iqbama311server.mysql.database.azure.com
    final static String SQL_SERVER_URL = "jdbc:mysql://bandrowskicsc311server.mysql.database.azure.com";
    public final static String DB_URL = SQL_SERVER_URL + "/" + DB_NAME;
    final static String USERNAME = "bandrowskiadmin";
    final static String PASSWORD = "Farmingdale24";

    // Method to connect to the database and initialize tables if not already created
    public boolean connectToDatabase() {
        boolean hasRegisteredUsers = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // First, connect to MySQL server and create the database if not created
            try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD);
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Connect to the specific database and create necessary tables
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

                // Create songs table
                String createSongsTable = "CREATE TABLE IF NOT EXISTS songs (" +
                        "song_id INT PRIMARY KEY AUTO_INCREMENT," +
                        "title VARCHAR(100) NOT NULL," +
                        "artist VARCHAR(50)," +
                        "album VARCHAR(50)," +
                        "file_url VARCHAR(255))";
                statement.executeUpdate(createSongsTable);

                // Create playlists table
                String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS playlists (" +
                        "playlist_id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(50) NOT NULL," +
                        "user_id INT," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))";
                statement.executeUpdate(createPlaylistsTable);

                // Create playlist_songs table
                String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS playlist_songs (" +
                        "playlist_id INT," +
                        "song_id INT," +
                        "PRIMARY KEY (playlist_id, song_id)," +//composite primary key ensure each song once in playlist
                        "FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id)," +
                        "FOREIGN KEY (song_id) REFERENCES songs(song_id))";
                statement.executeUpdate(createPlaylistSongsTable);

                // Check if any users are already registered
                ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
                if (resultSet.next()) {
                    hasRegisteredUsers = resultSet.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegisteredUsers;
    }

    // Method for registering a new user
    public boolean registerUser(String firstName, String lastName, String email, String password) {
        String insertUserSQL = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

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

    public String getUserFullName(String email) {
        String getUserSQL = "SELECT first_name, last_name FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(getUserSQL)) {

            pstmt.setString(1, email);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                return firstName + " " + lastName;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if no user is found
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
}
