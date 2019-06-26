package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Service for storing, retrieving and deleting answer pictures and avatars
 *
 * @author Johannes Koch
 */
@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageAPIController.class);

    private Path temp;
    private Path avatars;
    private Path answers;
    private int avatarSize;
    private int answerSize;
    @Value("${storage.avatars.imageType}")
    private String avatarType;
    @Value("${storage.answers.imageType}")
    private String answerType;
    @Value("${storage.avatars.default}")
    private String defaultAvatar;
    @Value("${storage.answers.default}")
    private String defaultAnswer;
    private ImageService imageService;

    /**
     * Initializes the {@link StorageService} with the properties specified in the application.properties file.
     *
     * @param root Root directory where files are stored.
     * @param temp  Directory for temporary files.
     * @param avatarSize Resolution for the {@link at.qe.sepm.skeleton.model.Player} avatars.
     * @param answerSize Resolution for the {@link at.qe.sepm.skeleton.model.Question} answers.
     * @param avatarEndpoint Path to storage of avatars.
     * @param answerEndpoint Path to storage of answers.
     */
    @Autowired
    public void initProperties(
            @Value("${storage.uploads.location}") String root,
            @Value("${storage.uploads.temporary}") String temp,
            @Value("${storage.avatars.minResolution}") String avatarSize,
            @Value("${storage.answers.minResolution}") String answerSize,
            @Value("${storage.api.avatars}") String avatarEndpoint,
            @Value("${storage.api.answers}") String answerEndpoint) {
        this.temp = Paths.get(temp);
        this.avatars = Paths.get(root).resolve(avatarEndpoint);
        this.answers = Paths.get(root).resolve(answerEndpoint);
        this.avatarSize = Integer.valueOf(avatarSize);
        this.answerSize = Integer.valueOf(answerSize);
    }

    /**
     * Autowires the {@link ImageService} on creation
     *
     * @param imageService {@link ImageService} is used to resize uploaded images.
     */
    @Autowired
    public FileSystemStorageService(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Initializes storage by creating storage directories
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        Files.createDirectories(temp);
        Files.createDirectories(avatars);
        Files.createDirectories(answers);
    }

    /**
     * Stores an avatar in the service
     *
     * @return filepath of avatar
     * @throws IOException
     */
    @Override
    public String storeAvatar(File file, String managerId) {
        return store(file, managerId, avatars, avatarSize, avatarType);
    }

    /**
     * Stores an answer in the service
     *
     * @param file to store
     * @param managerId
     * @param qSetId
     * @return filepath to answer
     */
    @Override
    public String storeAnswer(File file, String managerId, String qSetId) {
        return store(file, managerId + "/" + qSetId, answers, answerSize, answerType);
    }

    /**
     * First stores the stream contents in a temporary directory, resizes the image and
     * then saves it to {root}/{pathPrefix}/{uniqueFilename.ext}
     *
     * @param file File to store.
     * @param pathPrefix Subdirectory within the root directory, will be created.
     * @param root  Root directory.
     * @param size  Resolution of stored file.
     * @return Path to File as String.
     */
    private String store(File file, String pathPrefix, Path root, int size, String type){
        String extension = FilenameUtils.getExtension(file.getName());

        Path tempFile = null;
        Path filePath;
        try(InputStream inputStream = new FileInputStream(file)) {
            tempFile = Files.createTempFile(temp, "answer", "." + extension);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            Files.createDirectories(root.resolve(pathPrefix));

            filePath = imageService.resizeImage(tempFile, root.resolve(pathPrefix), size, size, type);
            if(filePath == null) return null;
        } catch (IOException e){
            log.error("Failed to store uploaded file in temporary directory");
            return null;
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e){
                log.warn("Couldn't delete temporary file");
            }
        }

        return pathPrefix + "/" + filePath.getFileName();
    }

    /**
     * Loads a stored avatar or returns a default
     *
     * @param avatar
     * @return
     */
    @Override
    public Path loadAvatar(String avatar) {
        Path path = avatars.resolve(avatar);
        if(Files.exists(path)){
            return path;
        }
        return avatars.resolve(defaultAvatar);
    }

    /**
     * Loads a stored answer or returns a default
     *
     * @param answer
     * @return
     */
    @Override
    public Path loadAnswer(String answer) {
        Path path = answers.resolve(answer);
        if(Files.exists(path)){
            return path;
        }
        return answers.resolve(defaultAnswer);
    }

    /**
     * Deletes a stored avatar
     *
     * @param avatar file to be deleted
     */
    @Override
    public void deleteAvatar(String avatar) {
        try {
            Files.deleteIfExists(avatars.resolve(avatar));
        } catch (IOException e){
            log.error("Could not delete avatar " + avatar);
        }
    }

    /**
     * Deletes a stored answer
     *
     * @param answer file to be deleted
     */
    @Override
    public void deleteAnswer(String answer) {
        try {
            Files.deleteIfExists(answers.resolve(answer));
        } catch (IOException e){
            log.error("Could not delete avatar " + answer);
        }
    }

    /**
     * Deletes all answers of the {@link at.qe.sepm.skeleton.model.QuestionSet}.
     * @param managerId
     * @param questionSetId
     */
    @Override
    public void deleteAllAnswersOfQuestionSet(String managerId, String questionSetId) {
        FileSystemUtils.deleteRecursively(answers.resolve(managerId + "/" + questionSetId).toFile());
    }
}