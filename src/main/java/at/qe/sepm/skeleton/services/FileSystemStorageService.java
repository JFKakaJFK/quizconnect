package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.ui.controllers.ImageAPIController;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     * @param imageService
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
     * Stores a file in the service
     *
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAvatar(InputStream inputStream, String filename, String managerId) {
        return store(inputStream, filename, managerId, avatars, avatarSize, avatarType);
    }

    /**
     * Stores a file in the service
     *
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAnswer(InputStream inputStream, String filename, String managerId) {
        return store(inputStream, filename, managerId, answers, answerSize, answerType);
    }

    /**
     * First stores the stream contents in a temporary directory, resizes the image and
     * then saves it to {root}/{managerId}/{uniqueFilename.ext}
     * @param inputStream
     * @param filename
     * @param managerId
     * @param root
     * @param size
     * @return
     */
    private String store(InputStream inputStream, String filename, String managerId, Path root, int size, String type){
        String extension = FilenameUtils.getExtension(filename);

        Path tempFile = null;
        Path filePath;
        try {
            tempFile = Files.createTempFile(temp, "answer", "." + extension);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            Files.createDirectories(root.resolve(managerId));

            filePath = imageService.resizeImage(tempFile, root.resolve(managerId), size, size, type);
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

        return managerId + "/" + filePath.getFileName();
    }

    /**
     * loads a stored avatar or returns a default
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
     * loads a stored answer or returns a default
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
     * deletes a stored avatar
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
     * deletes a stored answer
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
}