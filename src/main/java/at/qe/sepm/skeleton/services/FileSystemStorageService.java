package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.configs.StorageProperties;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * Service for storing, retrieving and deleting files
 *
 * @author Johannes Koch
 */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final String avatarPrefix;
    private final String answerPrefix;

    /**
     * The {@link StorageProperties#location} is used as storage directory
     *
     * @param properties
     */
    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.avatarPrefix = properties.getAvatarPrefix();
        this.answerPrefix = properties.getAnswerPrefix();
    }

    /**
     * Stores a {@link UploadedFile} in the service
     *
     * @param uploadedFile file to store
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAvatar(UploadedFile uploadedFile, String managerId) throws IOException {
        Path uploadPath = Paths.get(managerId + "/" + avatarPrefix);
        // TODO: compression options
        return store(uploadedFile, uploadPath);
    }

    /**
     * Stores a {@link UploadedFile} in the service
     *
     * @param uploadedFile file to store
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAnswer(UploadedFile uploadedFile, String managerId) throws IOException {
        // TODO: maybe add questionSet in path?
        Path uploadPath = Paths.get(managerId + "/" + avatarPrefix);
        // TODO: compression options
        return store(uploadedFile, uploadPath);
    }

    /**
     * Stores a {@link UploadedFile} in the service
     *
     * @param uploadedFile file to store
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    // TODO: use for both avatar & answer, add compression options
    private String store(UploadedFile uploadedFile, Path uploadPath) throws IOException {
        String filename = FilenameUtils.getBaseName(uploadedFile.getFileName());
        String extension = FilenameUtils.getExtension(uploadedFile.getFileName());

        Files.createDirectories(rootLocation.resolve(uploadPath.toString()));
        Path file = Files.createTempFile(rootLocation.resolve(uploadPath.toString()), filename + "-", "." + extension);

        // TODO: compress: write to temp file in /temp compress & write output to desired location

        InputStream input = uploadedFile.getInputstream();
        Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        return uploadPath.resolve(file.getFileName().toString()).toString();
    }

    /**
     * Retrieves a previously stored file
     *
     * @param filename to retrieve
     * @return {@link Path} to file
     */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * Deletes all files stored in the service
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * Initializes storage by creating storage directory
     *
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        Files.createDirectories(rootLocation);
    }

    /**
     * Deletes file from the service
     *
     * @param filename of file to be deleted
     * @throws IOException
     */
    @Override
    public void deleteFile(String filename) throws IOException {
        Files.deleteIfExists(rootLocation.resolve(filename));
    }
}