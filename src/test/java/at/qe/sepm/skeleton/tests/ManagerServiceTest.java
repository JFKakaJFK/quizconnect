package at.qe.sepm.skeleton.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.UserService;

/**
 * Tests for the ManagerService.
 * 
 * @author Lorenz_Smidt
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ManagerServiceTest
{
	@Autowired
	ManagerService managerService;
	
	@Autowired
	UserService userService;
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testLoadAllManagers()
	{
		Collection<Manager> managers = managerService.getAllManagers();
		assertEquals("Wrong number of managers loaded!", 2, managers.size());
		
		for (Manager manager : managers)
		{
			assertNotNull("Manager user is null!", manager.getUser());
		}
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetManagerById()
	{
		Manager manager = managerService.getManagerById(101);
		assertNotNull("Manager not loaded!", manager);
		assertEquals("Wrong Manager loaded!", new Integer(101), manager.getId());
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testUpdateManager()
	{
		Manager manager = managerService.getManagerById(102);
		assertNotNull("Manager not loaded!", manager);
		
		manager.setEmail("newEmail");
		managerService.saveManager(manager);
		
		Manager manager2 = managerService.getManagerById(102);
		assertNotNull("Manager2 not loaded!", manager2);
		assertEquals("Email not saved!", "newEmail", manager2.getEmail());
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testUpdateManagerNoUser()
	{
		Manager manager = managerService.getManagerById(101);
		assertNotNull("Manager not loaded!", manager);
		
		manager.setUser(null);
		managerService.saveManager(manager);
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testUpdateManagerNoEmail()
	{
		Manager manager = managerService.getManagerById(101);
		assertNotNull("Manager not loaded!", manager);
		
		manager.setEmail(null);
		managerService.saveManager(manager);
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user3", authorities = {})
	public void testCreateManager()
	{
		Manager manager = new Manager();
		manager.setEmail("newEmail");
		manager.setInstitution("newInstitution");
		
		Manager savedManager = managerService.saveNewManager(manager, "pw");
		assertNotNull("Saved manager is null!", savedManager);
		
		assertEquals("Manager email is wrong!", "newEmail", savedManager.getEmail());
		assertEquals("Manager institution is wrong!", "newInstitution", savedManager.getInstitution());
		
		User user = savedManager.getUser();
		assertNotNull("Manager user is null!", user);
		
		assertEquals("User role is wrong!", UserRole.MANAGER, user.getRole());
		assertEquals("User username is wrong!", "newEmail", user.getUsername());
		assertEquals("User password is wrong!", "pw", user.getPassword());
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user3", authorities = {})
	public void testCreateManagerNoEmail()
	{
		Manager manager = new Manager();
		manager.setInstitution("newInstitution");
		
		managerService.saveNewManager(manager, "pw");
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user3", authorities = {})
	public void testCreateManagerNoPassword()
	{
		Manager manager = new Manager();
		manager.setEmail("newEmail");
		manager.setInstitution("newInstitution");
		
		managerService.saveNewManager(manager, null);
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user3", authorities = { "MANAGER" })
	public void testCreateManagerAlreadyUser()
	{
		Manager manager = new Manager();
		manager.setEmail("newEmail");
		manager.setInstitution("newInstitution");
		
		User user = userService.loadUser("user1");
		manager.setUser(user);
		
		managerService.saveNewManager(manager, "pw");
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user3", authorities = { "MANAGER" })
	public void testCreateManagerWrongFunction()
	{
		Manager manager = new Manager();
		manager.setEmail("newEmail");
		manager.setInstitution("newInstitution");
		
		managerService.saveManager(manager);
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testDeleteManager()
	{
		Manager manager = managerService.getManagerById(102);
		assertNotNull("Manager not loaded!", manager);
		
		managerService.deleteManager(manager);
		
		Manager manager2 = managerService.getManagerById(102);
		assertNull("Manager not deleted!", manager2);
	}
}