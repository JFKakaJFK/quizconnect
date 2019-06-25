package at.qe.sepm.skeleton.utils;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Class for authentication (used when accessing data with required security context)
 */
public final class AuthenticationUtil
{

    // Ensures that this class cannot be instantiated
    private AuthenticationUtil() {}

    public static void clearAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void configureAuthentication(String role) {
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", role, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
