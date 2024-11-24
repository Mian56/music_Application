package model;

import com.azure.storage.blob.BlobClient;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetadataExtractor {

    public static Map<String, String> extractMetadata(File audioFile) {
        Map<String, String> metadata = new HashMap<>();

        try {
            AudioFile audio = AudioFileIO.read(audioFile);
            Tag tag = audio.getTag();

            // Extract metadata fields
            metadata.put("title", tag.getFirst(FieldKey.TITLE));
            metadata.put("artist", tag.getFirst(FieldKey.ARTIST));
            metadata.put("album", tag.getFirst(FieldKey.ALBUM));
            metadata.put("composer", tag.getFirst(FieldKey.COMPOSER));
            metadata.put("duration", String.valueOf(audio.getAudioHeader().getTrackLength())); // Duration in seconds
        } catch (CannotReadException | NullPointerException e) {
            System.err.println("Error extracting metadata: " + e.getMessage());
            e.printStackTrace();
        } catch (TagException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return metadata;
    }

    public static Metadata extractMetadataDB(BlobClient blobClient, String blobName) {
        try {
            Map<String, String> metadata = blobClient.getProperties().getMetadata();
            String title = metadata.getOrDefault("title", "Unknown Title");
            String artist = metadata.getOrDefault("artist", "Unknown Artist");
            String duration = metadata.getOrDefault("duration", "0");
            String album = metadata.getOrDefault("album", "Unknown Album");
            String genre = metadata.getOrDefault("genre", "Unknown Genre");

            // Pass blobName to Metadata
            return new Metadata(title,blobName,artist, duration, album, genre);
        } catch (Exception e) {
            System.err.println("Error fetching metadata for blob: " + blobName + " - " + e.getMessage());
            return new Metadata("Na", "Na", "0", "Na", "Na", blobName);
        }
    }
}
