package at.qe.sepm.skeleton.ui.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Bean for validating different kinds of inputs.
 */
@Component
@Scope("view")
public class ValidationBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Validates an email.
     *
     * @param email
     * 		String to check.
     * @return <code>true</code> if string contains a valid e-mail address
     */
    public boolean isValidEmail(String email) {
        if (email != null) {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);
            return pat.matcher(email).matches();
        }
        return false;
    }
    
    /**
     * Validates a number.
     *
     * @param numerics
     * 		String to check.
     * @return <code>true</code> if string only contains digits 0-9
     */
    public boolean isValidPositiveNumber(String numerics) {
        if (numerics != null) {
            String numericsRegex = "^\\d+$";
            Pattern pat = Pattern.compile(numericsRegex);
            return pat.matcher(numerics).matches();
        }
        return false;
    }
    
    /**
     * Validates text using a regular expression
     *
     * @param safetext
     * 		String to check.
     * @return <code>true</code> if string only contains lowercase letters, uppercase letters or digits.
     */
    public boolean isValidText (String safetext) {
        if (safetext != null) {
            String safetextRegex = "^[a-zA-Z0-9 .,;-_€@$äÄöÖüÜ!?#&=]+$";
            Pattern pat = Pattern.compile(safetextRegex);
            return pat.matcher(safetext).matches();
        }
        return false;
    }

    /**
     * Validates text using a regular expression and checks for maximal length.
     *
     * @param safetext
     * 		String to check.
     * @param maxlength
     *      Maximal length.
     * @return <code>true</code> if string only contains lowercase letters, uppercase letters or digits.
     */
    public boolean isValidText (String safetext, int maxlength) {
        return isValidText(safetext) && safetext.length() <= maxlength;
    }

    /** Validates passwords using a regular expression
     *
     * @param password
     * @return <code>true</code> if string has more than 5 characters
     */
    public boolean isValidPassword (String password) {
        if (password != null) {
            //String passwordRegex = "^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[\\p{L}\\p{N}\\p{P}]{6,}$";
            String passwordRegex = "^.{5,}$"; //if someone wants to set a password consisting of 5 spaces, that's fine
            Pattern pat = Pattern.compile(passwordRegex);
            return pat.matcher(password).matches();
        }
        return false;
    }


    /** Checks if two passwords are equal
     *
     * @param password
     * @return <code>true</code> if string has more than 5 characters
     */
    public boolean isValidPassword (String password, String repeatPassword) {
        if (password != null) {
            String passwordRegex = "^.{5,}$"; //if someone wants to set a password consisting of 5 spaces, that's fine
            Pattern pat = Pattern.compile(passwordRegex);
            if (repeatPassword != null && pat.matcher(password).matches()) {
                return password.equals(repeatPassword);
            }
        }
        return false;
    }

    /**
     * Returns true if the extension of the filename is a supported image.
     *
     * @param pictureString
     *      Filename of a potential image.
     * @param maxlength
     *      Maximal length of filename.
     * @return <code>true</code> if the extension of the filename is a supported image.
     */
    public boolean isValidPictureString (String pictureString, int maxlength) {
        if (pictureString != null && pictureString.length() <= maxlength) {
            String passwordRegex = "^[0-9]+\\/[0-9]+\\/.*(\\.png|\\.jpg)$";
            Pattern pat = Pattern.compile(passwordRegex);
            return pat.matcher(pictureString).matches();
        }
        return false;
    }
}