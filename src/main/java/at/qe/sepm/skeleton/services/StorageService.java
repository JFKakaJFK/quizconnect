package at.qe.sepm.skeleton.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface of a service for storing and retrieving avatar and answer pictures.
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
     * @param managerId id of {@link at.qe.sepm.skeleton.model.Player}s {@link at.qe.sepm.skeleton.model.Manager}
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAvatar(File file, String managerId) throws IOException;

    /**
     * Stores an answer {@link File} in the service
     *
     * @param file to store
     * @param managerId id of the {@link at.qe.sepm.skeleton.model.Manager} storing the file.
     * @param qSetId id of the {@link at.qe.sepm.skeleton.model.QuestionSet} the picture is for.
     * @return filename of the stored file, needed for retrieval of stored file
     * @throws IOException
     */
    String storeAnswer(File file, String managerId, String qSetId) throws IOException;

    /**
     * Returns a stored avatar
     *
     * @param avatar filename of previously stored avatar
     * @return Path to file
     */
    Path loadAvatar(String avatar);

    /**
     * Returns a stored answer
     *
     * @param answer filename of previously stored answer
     * @return Path to file
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
