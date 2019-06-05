package at.qe.sepm.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Scope("request")
public class ValidationBean {

    /**
     *
     * @param email
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
     *
     * @param numerics
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

    /** Validates text using a regular expression
     *
     * @param safetext
     * @return <code>true</code> if string only contains lowercase letters, uppercase letters or digits.
     */
    public boolean isValidText (String safetext) {
        if (safetext != null) {
            String safetextRegex = "^[a-zA-Z0-9 .-]+$";
            Pattern pat = Pattern.compile(safetextRegex);
            return pat.matcher(safetext).matches();
        }
        return false;
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
}