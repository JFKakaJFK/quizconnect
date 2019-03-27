package at.qe.sepm.skeleton.services;

import org.primefaces.model.UploadedFile;

import java.io.IOException;
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
     * @param file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAvatar(UploadedFile file, String managerId) throws IOException;

    /**
     * Stores an answer file {@link UploadedFile} in the service
     *
     * @param file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAnswer(UploadedFile file, String managerId) throws IOException;

    /**
     * Loads a previously stored file
     *
     * @param filename to retrieve
     * @return Path to file
     */
    Path load(String filename);

    /**
     * Deletes all stored files from the service
     */
    void deleteAll();

    /**
     * Deletes a specific file from the service
     *
     * @param filename of file to be deleted
     * @throws IOException
     */
    void deleteFile(String filename) throws IOException;
}
