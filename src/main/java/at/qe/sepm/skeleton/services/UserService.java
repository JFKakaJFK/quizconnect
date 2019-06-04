package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

/**
 * Service for accessing and manipulating user data.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@Component
@Scope("application")
public class UserService
{
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * @return A collection of all users in the database.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Collection<User> getAllUsers()
	{
		return userRepository.findAll();
	}
	
	/**
	 * Loads a single user identified by its username.
	 *
	 * @param username
	 * 		the username to search for
	 * @return the user with the given username
	 */
	@PreAuthorize("hasAuthority('MANAGER') or principal.username eq #username")
	public User loadUser(String username)
	{
		return userRepository.findFirstByUsername(username);
	}
	
	/**
	 * @param username
	 * 		Username of a User.
	 * @return True if a user with username exists, false otherwise.
	 */
	public boolean existsUser(String username)
	{
		return userRepository.findFirstByUsername(username) != null;
	}
	
	/**
	 * Saves the {@link User}. Performs some consistency checks, but does not check if User is associated with a Player / Manager.
	 *
	 * @param user
	 * 		the user to save
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
	 * Deletes the user.
	 *
	 * @param user
	 * 		User to be deleted.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public void deleteUser(User user)
	{
		userRepository.delete(user);
	}
	
}
