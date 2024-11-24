package datab;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import model.Metadata;
import model.MetadataExtractor;

public class MusicDB {

    private BlobContainerClient containerClient;

    public MusicDB() {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=musicappdb;AccountKey=/TxkG8DnJ6NGWCEnv/82FiqesEi04JLZ/s6qd5Ox78qGJuxETnxCrpVs6C42jsmTzNUQ65iZ5cLn+AStfJBFbw==;EndpointSuffix=core.windows.net")
                .containerName("media-files")
                .buildClient();
    }

    public void uploadFile(String filePath, String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath);
    }

    public BlobContainerClient getContainerClient() {
        return containerClient;
    }

    public String listAllBlobsMetadataOnly() {
        StringBuilder metadataBuilder = new StringBuilder();

        // Add column headers
        metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n", "Song Name", "Artist", "Duration", "Album", "Genre"));
        metadataBuilder.append("-------------------------------------------------------------------------------------------\n");

        // Iterate over each blob item in the container
        for (BlobItem blobItem : containerClient.listBlobs()) {
            try {
                // Create a BlobClient for the current blob
                BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());

                // Use MetadataExtractor to extract metadata from the blob
                Metadata metadata = MetadataExtractor.extractMetadataDB(blobClient, blobItem.getName());

                // Append metadata row
                metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n",
                        metadata.getSongName(), metadata.getArtist(), metadata.getDuration(),
                        metadata.getAlbum(), metadata.getGenre()));
            } catch (Exception e) {
                // Append an error row for this blob
                metadataBuilder.append(String.format("%-20s %-20s %-10s %-20s %-20s\n",
                        "Na", "Na", "Na", "Na", "Na"));
            }
        }

        return metadataBuilder.toString();
    }



}