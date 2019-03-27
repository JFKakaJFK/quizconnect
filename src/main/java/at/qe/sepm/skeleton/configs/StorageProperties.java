package at.qe.sepm.skeleton.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Here the upload directory is defined
 */
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files, should be outside of the expanded
     * WAR file for production, see https://stackoverflow.com/questions/8885201/uploaded-image-only-available-after-refreshing-the-page
     */
    private String location = "upload-dir"; // should be changed to eg /var/webapp/uploads before deploy
    private String avatarPrefix = "avatar";
    private String answerPrefix = "answer";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatarPrefix() {
        return avatarPrefix;
    }

    public void setAvatarPrefix(String avatarPrefix) {
        this.avatarPrefix = avatarPrefix;
    }

    public String getAnswerPrefix() {
        return answerPrefix;
    }

    public void setAnswerPrefix(String answerPrefix) {
        this.answerPrefix = answerPrefix;
    }
}
