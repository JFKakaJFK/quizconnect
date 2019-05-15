package at.qe.sepm.skeleton.tests;

import org.junit.Assert;
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
 * Some very basic tests for {@link UserService}.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserServiceTest {

    @Autowired
    UserService userService;
	
	@Autowired
	ManagerService managerService;

    @Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testDatainitialization() {
		Assert.assertEquals("Insufficient amount of users initialized for test data source", 21, userService.getAllUsers().size());
        for (User user : userService.getAllUsers()) {
			if ("user1".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user1\" does not have role MANAGER", user.getRole().equals(UserRole.MANAGER));
				Assert.assertNotNull("User \"user1\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user2".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user2\" does not have role MANAGER", user.getRole().equals(UserRole.MANAGER));
				Assert.assertNotNull("User \"user2\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user3".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user3\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"user3\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user4".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user4\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"user4\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user5".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user5\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"user5\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user6".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user6\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"user6\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("user7".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"user7\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"user7\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("veryLongUserName".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"veryLongUserName\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"veryLongUserName\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Sidhu Moose Wala".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Sidhu Moose Wala\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Sidhu Moose Wala\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Elon Musk".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Elon Musk\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Elon Musk\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Jeff Bezos".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Jeff Bezos\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Jeff Bezos\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Benedict Cucumberbatch".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Benedict Cucumberbatch\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Benedict Cucumberbatch\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Kim Jong Un".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Kim Jong Un\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Kim Jong Un\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Sauron".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Sauron\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Sauron\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Bilbo Baggins".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Bilbo Baggins\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Bilbo Baggins\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Harry Potter".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Harry Potter\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Harry Potter\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Donald J. Trump".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Donald J. Trump\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Donald J. Trump\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Barack Obama".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Barack Obama\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Barack Obama\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Voldemort".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Voldemort\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Voldemort\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Freddy Mercury".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Freddy Mercury\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Freddy Mercury\" does not have a createDate defined", user.getCreateDate());
			}
			else if ("Heiner Bernadinho".equals(user.getUsername()))
			{
				Assert.assertTrue("User \"Heiner Bernadinho\" does not have role PLAYER", user.getRole().equals(UserRole.PLAYER));
				Assert.assertNotNull("User \"Heiner Bernadinho\" does not have a createDate defined", user.getCreateDate());
			}
			else
			{
                Assert.fail("Unknown user \"" + user.getUsername() + "\" loaded from test data source via UserService.getAllUsers");
            }
        }
    }

    @DirtiesContext
    @Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testDeleteUser() {
		User adminUser = userService.loadUser("user1");
		Assert.assertNotNull("Manager user could not be loaded from test data source", adminUser);
		User toBeDeletedUser = userService.loadUser("user2");
		Assert.assertNotNull("User2 could not be loaded from test data source", toBeDeletedUser);

        userService.deleteUser(toBeDeletedUser);

		Assert.assertEquals("No user has been deleted after calling UserService.deleteUser", 20, userService.getAllUsers().size());
		User deletedUser = userService.loadUser("user2");
		Assert.assertNull("Deleted User2 could still be loaded from test data source via UserService.loadUser", deletedUser);

        for (User remainingUser : userService.getAllUsers()) {
			Assert.assertNotEquals("Deleted User2 could still be loaded from test data source via UserService.getAllUsers", toBeDeletedUser.getUsername(),
					remainingUser.getUsername());
        }
    }

    @DirtiesContext
    @Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testUpdateUser() {
		User adminUser = userService.loadUser("user1");
		Assert.assertNotNull("Manager user could not be loaded from test data source", adminUser);
		User toBeSavedUser = userService.loadUser("user2");
		Assert.assertNotNull("User2 could not be loaded from test data source", toBeSavedUser);

		toBeSavedUser.setEnabled(false);
        userService.saveUser(toBeSavedUser);

		User freshlyLoadedUser = userService.loadUser("user2");
		Assert.assertNotNull("User2 could not be loaded from test data source after being saved", freshlyLoadedUser);
		Assert.assertEquals("User \"user2\" does not have a the correct enabled attribute stored being saved", false, freshlyLoadedUser.isEnabled());
    }

    @DirtiesContext
    @Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testCreateUser() {
		User adminUser = userService.loadUser("user1");
		Assert.assertNotNull("Manager user could not be loaded from test data source", adminUser);

		Manager manager = managerService.getManagerById(101);
		
        User toBeCreatedUser = new User();
        toBeCreatedUser.setUsername("newuser");
        toBeCreatedUser.setPassword("passwd");
        toBeCreatedUser.setEnabled(true);
		toBeCreatedUser.setManager(manager);
		userService.saveUser(toBeCreatedUser);

        User freshlyCreatedUser = userService.loadUser("newuser");
        Assert.assertNotNull("New user could not be loaded from test data source after being saved", freshlyCreatedUser);
        Assert.assertEquals("User \"newuser\" does not have a the correct username attribute stored being saved", "newuser", freshlyCreatedUser.getUsername());
        Assert.assertEquals("User \"newuser\" does not have a the correct password attribute stored being saved", "passwd", freshlyCreatedUser.getPassword());
		Assert.assertTrue("User \"newuser\" does not have role MANAGER", freshlyCreatedUser.getRole().equals(UserRole.MANAGER));
        Assert.assertNotNull("User \"newuser\" does not have a createDate defined after being saved", freshlyCreatedUser.getCreateDate());
    }

	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testExceptionForEmptyUsername() {
        User toBeCreatedUser = new User();
		userService.saveUser(toBeCreatedUser);
    }

    @Test(expected = org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class)
    public void testUnauthenticateddLoadUsers() {
        for (User user : userService.getAllUsers()) {
            Assert.fail("Call to userService.getAllUsers should not work without proper authorization");
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "user", authorities = { "PLAYER" })
    public void testUnauthorizedLoadUsers() {
        for (User user : userService.getAllUsers()) {
            Assert.fail("Call to userService.getAllUsers should not work without proper authorization");
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testUnauthorizedLoadUser() {
		User user = userService.loadUser("user1");
        Assert.fail("Call to userService.loadUser should not work without proper authorization for other users than the authenticated one");
    }

	@WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testAuthorizedLoadUser() {
		User user = userService.loadUser("user2");
		Assert.assertEquals("Call to userService.loadUser returned wrong user", "user2", user.getUsername());
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "user3", authorities = { "PLAYER" })
    public void testUnauthorizedDeleteUser() {
		User user = userService.loadUser("user3");
		Assert.assertEquals("Call to userService.loadUser returned wrong user", "user3", user.getUsername());
		userService.deleteUser(user);
    }

}
