package at.qe.sepm.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Bean for validating different kinds of inputs.
 */
@Component
@Scope("request")
public class ValidationBean {
    
    /**
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
            String safetextRegex = "^[a-zA-Z0-9 .-]+$";
            Pattern pat = Pattern.compile(safetextRegex);
            return pat.matcher(safetext).matches();
        }
        return false;
    }
    
    /**
     * Validates passwords using a regular expression
     *
     * @param password
     * 		String to check.
     * @return <code>true</code> if string has more than 6 characters and contains at least one letter and one number
     */
    public boolean isValidPassword (String password) {
        if (password != null) {
            String passwordRegex = "^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,}$";
            Pattern pat = Pattern.compile(passwordRegex);
            return pat.matcher(password).matches();
        }
        return false;
    }

}