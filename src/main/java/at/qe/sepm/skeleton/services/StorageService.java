package at.qe.sepm.skeleton.services;

import org.primefaces.model.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Service interface for storing, retrieving and deleting files
 *
 * @author Johannes Koch
 */
public interface StorageService {

    /**
     * Prepares the service for storing files
     *
     * @throws IOException
     */
    void init() throws IOException;

    /**
     * Stores a user avatar {@link UploadedFile} in the service
     *
     * @param inputStream of file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAvatar(InputStream inputStream, String filename, String managerId) throws IOException;

    /**
     * Stores an answer file {@link UploadedFile} in the service
     *
     * @param inputStream of file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAnswer(InputStream inputStream, String filename, String managerId) throws IOException;

    /**
     * Returns a stored file
     *
     * @param filename
     * @return
     */
    Path load(String filename);

    /**
     * Deletes avatar from the service
     *
     * @param filename of file to be deleted
     * @throws IOException
     */
    void deleteFile(String filename) throws IOException;
}
