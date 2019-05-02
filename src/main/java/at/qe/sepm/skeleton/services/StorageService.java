package at.qe.sepm.skeleton.services;

import java.io.File;
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
     * Stores a user avatar {@link File} in the service
     *
     * @param file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAvatar(File file, String managerId) throws IOException;

    /**
     * Stores an answer {@link File} in the service
     *
     * @param file to store
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAnswer(File file, String managerId) throws IOException;

    /**
     * Returns a stored avatar
     *
     * @param avatar
     * @return
     */
    Path loadAvatar(String avatar);

    /**
     * Returns a stored answer
     *
     * @param answer
     * @return
     */
    Path loadAnswer(String answer);

    /**
     * Deletes avatar from the service
     *
     * @param avatar file to be deleted
     * @throws IOException
     */
    void deleteAvatar(String avatar);
    /**
     * Deletes answer from the service
     *
     * @param answer file to be deleted
     * @throws IOException
     */
    void deleteAnswer(String answer);
}
