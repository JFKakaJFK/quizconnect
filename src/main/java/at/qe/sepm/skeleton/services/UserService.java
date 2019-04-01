package at.qe.sepm.skeleton.services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.UserRepository;

/**
 * Service for accessing and manipulating user data.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@Component
@Scope("application")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a collection of all users.
     *
     * @return
     */
	@PreAuthorize("hasAuthority('MANAGER')")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Loads a single user identified by its username.
     *
     * @param username the username to search for
     * @return the user with the given username
     */
	@PreAuthorize("hasAuthority('MANAGER') or principal.username eq #username")
    public User loadUser(String username) {
        return userRepository.findFirstByUsername(username);
    }

    /**
	 * Saves the {@link User}. Does not perform sanity checks (e.g. if either manager or player set).
	 *
	 * @param user
	 *            the user to save
	 * @return the updated user. Use for all further operations.
	 */
	public User saveUser(User user) throws IllegalArgumentException
	{
		if (user.isNew())
		{
			user.setCreateDate(new Date());
        }
		
		return userRepository.save(user);
    }

    /**
     * Deletes the user.
     *
     * @param user the user to delete
     */
	@PreAuthorize("hasAuthority('MANAGER')")
    public void deleteUser(User user) {
        userRepository.delete(user);
        // :TODO: write some audit log stating who and when this user was permanently deleted.
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

}
