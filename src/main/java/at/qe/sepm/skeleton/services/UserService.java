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
import at.qe.sepm.skeleton.model.UserRole;
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
	 * Returns true if a user with username exists, false otherwise.
	 * 
	 * @param username
	 * @return
	 */
	public boolean existsUser(String username)
	{
		return userRepository.findFirstByUsername(username) != null;
	}

    /**
	 * Saves the {@link User}. Performs some consistency checks, but does not check if User is associated with a Player / Manager.
	 *
	 * @param user
	 *            the user to save
	 * @return the updated user. Use for all further operations.
	 */
	public User saveUser(User user) throws IllegalArgumentException
	{
		if (user.getUsername() == null)
			throw new IllegalArgumentException("User username cannot be null!");
		else if (user.getPassword() == null)
			throw new IllegalArgumentException("User password cannot be null!");
		
		if (user.isNew())
		{
			user.setCreateDate(new Date());
        }

		return userRepository.save(user);
    }
	
	/**
	 * Saves a new User associated to a Manager. Allows saving of a new Manager without permissions.
	 * 
	 * @param user
	 */
	public void saveNewManager(User user) throws IllegalArgumentException
	{
		if (!user.isNew())
			throw new IllegalArgumentException("Can only save new Users with saveNewManager!");
		if (user.getManager() == null)
			throw new IllegalArgumentException("Can only save Users with associated Manager using saveNewManager!");
		if (user.getRole() != UserRole.MANAGER)
			throw new IllegalArgumentException("Can only save Users with role MANAGER using saveNewManager!");
		
		user.setCreateDate(new Date());
		
		userRepository.save(user);
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
