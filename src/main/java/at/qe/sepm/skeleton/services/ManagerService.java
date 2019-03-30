package at.qe.sepm.skeleton.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Manager;
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
	 * Saves the {@link Manager} to the database. Throws an IllegalArgumentException if any consistency checks fail.
	 * 
	 * @param manager
	 * @throws IllegalArgumentException
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public void saveManager(Manager manager) throws IllegalArgumentException
	{
		if (manager.getUser() == null)
			throw new IllegalArgumentException("Manager user cannot be null!");
		else if (manager.getEmail() == null)
			throw new IllegalArgumentException("Manager email cannot be null!");
		
		managerRepository.save(manager);
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