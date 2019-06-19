package at.qe.sepm.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;

/**
 * Bean to help with some password functionalism's.
 *
 * @author Johannes Spies
 */

@Component
@Scope("request")
public class PasswordBean implements Serializable {
    
    /**
     * Uses the BCryptPasswordEncoder to encode the password
     *
     * @param password
     * 		Password to be encoded.
     * @return The encoded password.
     */
    public String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
