package at.qe.sepm.skeleton.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private Path rootLocation;
    private Path temp;
    private Path thumbs;
    private int thumbSize;
    private String avatarPrefix;
    private String answerPrefix;
    private ImageService imageService;

    @Autowired
    public void initProperties(
            @Value("${storage.uploads.location}") String location,
            @Value("${storage.uploads.temporary}") String temp,
            @Value("${storage.thumbnails.location}") String thumbs,
            @Value("${storage.thumbnails.minResolution}") String thumbSize,
            @Value("${storage.prefixes.avatar}") String avatarPrefix,
            @Value("${storage.prefixes.answer}") String answerPrefix){
        this.rootLocation = Paths.get(location);
        this.temp = Paths.get(temp);
        this.thumbs = Paths.get(thumbs);
        this.thumbSize = Integer.valueOf(thumbSize);
        this.avatarPrefix = avatarPrefix;
        this.answerPrefix = answerPrefix;
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
     * Stores a file in the service
     *
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAvatar(InputStream inputStream, String filename, String managerId) throws IOException {
        Path uploadPath = Paths.get(managerId + "/" + avatarPrefix);
        return store(inputStream, filename, uploadPath, thumbSize);
    }

    /**
     * Stores a file in the service
     *
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    @Override
    public String storeAnswer(InputStream inputStream, String filename, String managerId) throws IOException {
        Path uploadPath = Paths.get(managerId + "/" + answerPrefix);
        return store(inputStream, filename, uploadPath, 400);
    }

    /**
     * Stores a in the service
     *
     * @param inputStream of file to store
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    // no unit test, probably unnecessary
    @Deprecated
    private String store(InputStream inputStream, String filename, Path uploadPath) throws IOException {
        String name = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);

        Files.createDirectories(rootLocation.resolve(uploadPath.toString()));
        Path file = Files.createTempFile(rootLocation.resolve(uploadPath.toString()), name + "-", "." + extension);

        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
        return uploadPath.resolve(file.getFileName().toString()).toString();
    }

    /**
     * Stores a user avatar in the service
     *
     * @param inputStream of file to store
     * @return filename of stored file, needed to retrieve file
     * @throws IOException
     */
    private String store(InputStream inputStream, String filename, Path uploadPath, final int size) throws IOException {
        String extension = FilenameUtils.getExtension(filename);

        Path tempFile = Files.createTempFile(temp, "upload", "." + extension);
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

        Files.createDirectories(thumbs.resolve(uploadPath.toString()));
        Path filePath = imageService.resizeToPNG(tempFile, thumbs.resolve(uploadPath), size, size);

        deleteFile(tempFile);

        return filePath.toString();
    }

    /**
     * Retrieves a previously stored file
     *
     * @param filename to retrieve
     * @return {@link Path} to file
     */
    @Override
    public Path load(String filename) {
        return Paths.get(filename);
    }

    /**
     * Deletes all files stored in the service
     */
    // no unit test, probably unnecessary
    @Deprecated
    //@Override
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
        Files.createDirectories(temp);
        Files.createDirectories(thumbs);
    }

    /**
     * Deletes a temporary file
     *
     * @param file
     * @throws IOException
     */
    private void deleteFile(Path file) throws IOException {
        Files.deleteIfExists(file);
    }

    /**
     * Deletes file from the service
     *
     * @param filename of file to be deleted
     * @throws IOException
     */
    @Override
    public void deleteFile(String filename) throws IOException {
        Files.deleteIfExists(Paths.get(filename));
    }
}