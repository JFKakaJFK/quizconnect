package at.qe.sepm.skeleton.services;

import java.util.Collection;

import at.qe.sepm.skeleton.model.QuestionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.ManagerRepository;

/**
 * Service for accessing and manipulating {@link Manager} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
@Component
@Scope("application")
public class ManagerService
{
	@Autowired
	ManagerRepository managerRepository;
	
	@Autowired
	UserService userService;
	
	/**
	 * Returns a Collection of all {@link Manager}s.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Collection<Manager> getAllManagers()
	{
		return managerRepository.findAll();
	}
	
	/**
	 * Returns a {@link Manager} based on its id.
	 * 
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Manager getManagerById(int id)
	{
		return managerRepository.findOne(id);
	}
	
	/**
	 * Returns the creator {@link Manager} of a {@link Player}.
	 * 
	 * @param player
	 * @return
	 */
	@PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER')")
	public Manager getManagerOfPlayer(Player player)
	{
		return managerRepository.findByCreatedPlayers(player);
	}

	/**
	 * Returns the creator {@link Manager} of a {@link QuestionSet}.
	 *
	 * @param questionSet
	 * @return
	 */
	@PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER')")
	public Manager getManagerOfQuestionSet(QuestionSet questionSet)
	{
		return managerRepository.findByCreatedQuestionSets(questionSet);
	}


	/**
	 * Saves the {@link Manager} to the database. Throws an IllegalArgumentException if any consistency checks fail. Use 'saveNewManager' to create a new Manager.
	 * 
	 * @param manager
	 * @throws IllegalArgumentException
	 * @return new Manager reference. Use for all further operations.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Manager saveManager(Manager manager) throws IllegalArgumentException
	{
		if (manager.isNew())
			throw new IllegalArgumentException("Use 'saveNewManager' to save new Managers!");
		else if (manager.getUser() == null)
			throw new IllegalArgumentException("Manager user cannot be null!");
		
		managerSanityCheck(manager);

		return managerRepository.save(manager);
	}
	
	/**
	 * Saves a new {@link Manager} to the database. Automatically generates a new User for the manager using the manager.email and password. Password is expected to be already encrypted. Performs
	 * consistency checks which may throw IllegalArgumentExceptions. For re-saving / updating managers use the 'saveManager' function.
	 * 
	 * @param manager
	 * @param password
	 * @throws IllegalArgumentException
	 * @return new Manager reference. Use for all further operations.
	 */
	public Manager saveNewManager(Manager manager, String password) throws IllegalArgumentException
	{
		if (!manager.isNew())
			throw new IllegalArgumentException("Manager to be saved must be new!");
		else if (password == null)
			throw new IllegalArgumentException("Password cannot be null!");
		else if (manager.getUser() != null)
			throw new IllegalArgumentException("Manager already has an associated User!");
		
		managerSanityCheck(manager);
		
		User newUser = new User();
		newUser.setUsername(manager.getEmail());
		newUser.setPassword(password);
		newUser.setEnabled(true);
		User savedUser = userService.saveUser(newUser);
		
		manager.setUser(savedUser); // set manager user
		Manager savedManager = managerRepository.save(manager); // save manager to DB
		
		savedUser.setManager(savedManager); // automatically sets user role
		userService.saveUser(savedUser); // update user in DB
		
		return savedManager;
	}
	
	/**
	 * Performs basic sanity checks on the manager. Throws an exception if any checks fail, terminates otherwise.
	 * 
	 * @param manager
	 * @throws IllegalArgumentException
	 */
	private void managerSanityCheck(Manager manager) throws IllegalArgumentException
	{
		if (manager.getEmail() == null)
			throw new IllegalArgumentException("Manager email cannot be null!");
	}
	
	/**
	 * Deletes the {@link Manager} from the database.
	 * 
	 * @param manager
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public void deleteManager(Manager manager)
	{
		managerRepository.delete(manager);
	}
	
}