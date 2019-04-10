package at.qe.sepm.skeleton.services;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Device;
import at.qe.sepm.skeleton.model.DeviceGroup;
import at.qe.sepm.skeleton.model.DeviceManual;
import at.qe.sepm.skeleton.model.DeviceType;
import at.qe.sepm.skeleton.model.Laboratory;
import at.qe.sepm.skeleton.model.Reservation;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.utils.AuthenticationUtil;

/**
 * Service for generating test data. For debug / testing purposes only. DISABLE ON REAL EXECUTE! If this is enabled, all functionality of the BGThread is disabled! Generated Reservations are not
 * necessarily valid in regards to constrains (device maxResDuration, lab opening hours, holidays, ...)
 * 
 * @author Lorenz_Smidt
 *
 */
@Component
@Scope("application")
public class DataGeneratorService
{
	public static final boolean enabled = false;
	
	// all numbers of entities to generate are multiplied by this (for large-scale testing)
	private final int countMultiplier = 1;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ReservationService reservationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LaboratoryService laboratoryService;
	
	@Autowired
	DeviceTypeService deviceTypeService;
	
	@Autowired
	DeviceService deviceService;
	
	@Autowired
	DeviceGroupService deviceGroupService;
	
	@Autowired
	DeviceManualService deviceManualService;
	
	/**
	 * Calls all sub-generator functions. Gets called from BGThreadManager on server startup.
	 */
	public void generateData()
	{
		if (!enabled)
		{
			return;
		}
		
		Random random = new Random();
		AuthenticationUtil.configureAuthentication("ADMIN");
		
		generateUsers(random.nextInt(20 * countMultiplier) + (30 * countMultiplier));
		
		generateLaboratories(random.nextInt(5 * countMultiplier) + (5 * countMultiplier));
		
		generateDeviceTypes(random.nextInt(30 * countMultiplier) + (40 * countMultiplier));
		
		generateDevices(random.nextInt(60 * countMultiplier) + (90 * countMultiplier));
		
		generateDeviceGroups(random.nextInt(30 * countMultiplier) + (20 * countMultiplier));
		
		generateDeviceManuals(random.nextInt(3 * countMultiplier) + (1 * countMultiplier));

		generateReservations(random.nextInt(120 * countMultiplier) + (60 * countMultiplier));
		
		AuthenticationUtil.clearAuthentication();
	}
	

	/**
	 * Generates count number of {@link User}s and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateUsers(int count)
	{
		PasswordEncoder pEncoder = new BCryptPasswordEncoder(6);
		Set<UserRole> roles = new HashSet<>();
		roles.add(UserRole.STUDENT);
		for (int i = 1; i <= count; i++)
		{
			User user = new User();
			user.setcNumber("cGEN00" + i);
			user.setUsername("genUser" + i);
			user.setEnabled(true);
			user.setEmail("genUser" + i + "@email.com");
			user.setPassword(pEncoder.encode("passwd" + i));
			user.setFirstName("genFirstName" + i);
			user.setLastName("genLastName" + i);
			user.setRoles(roles);

			userService.saveUser(user);
			
			if (i == (int) (count * 0.6))
			{
				roles.add(UserRole.EMPLOYEE);
			}
			else if (i == (int) (count * 0.95))
			{
				roles.add(UserRole.ADMIN);
			}
		}
		
		logger.info("> generated " + count + " users");
	}
	

	/**
	 * Generates count number of {@link Laboratory}s and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateLaboratories(int count)
	{
		Random random = new Random();
		for (int i = 1; i <= count; i++)
		{
			Laboratory laboratory = new Laboratory();
			
			laboratory.setName("genLab" + i);
			laboratory.setDescription("genLabDesc" + i);
			laboratory.setOpenWeekDaysStart(random.nextInt(2) + 2);
			laboratory.setOpenWeekDaysEnd(random.nextInt(3) + 5);
			laboratory.setOpenHoursStart((random.nextInt(6) + 4) * 100 + (random.nextInt(60)));
			laboratory.setOpenHoursEnd((random.nextInt(10) + 13) * 100 + (random.nextInt(60)));
			
			laboratoryService.saveLaboratory(laboratory);
		}

		logger.info("> generated " + count + " laboratories");
	}
	

	/**
	 * Generates count number of {@link DeviceType}s and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateDeviceTypes(int count)
	{
		for (int i = 1; i <= count; i++)
		{
			DeviceType type = new DeviceType();
			
			type.setName("genType" + i);
			type.setDescription("genTypeDesc" + i);
			
			deviceTypeService.saveDeviceType(type);
		}
		
		logger.info("> generated " + count + " device types");
	}
	

	/**
	 * Generates count number of {@link Device}s with randomly chosen types and Laboratories and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateDevices(int count)
	{
		Random random = new Random();
		List<Laboratory> labs = new LinkedList<>(laboratoryService.getAllLaboratories());
		List<DeviceType> types = new LinkedList<>(deviceTypeService.getAllDeviceTypes());
		for (int i = 1; i <= count; i++)
		{
			Device device = new Device();
			
			device.setName("genDevice" + i);
			device.setLaboratory(labs.get(random.nextInt(labs.size())));
			device.setType(types.get(random.nextInt(types.size())));
			device.setLocation("genDeviceLocation" + i);
			device.setMaxReservedMinutes(random.nextInt(6000) + 20);
			
			deviceService.saveDevice(device);
		}
		
		logger.info("> generated " + count + " devices");
	}
	

	/**
	 * Generates count number of {@link DeviceGroup}s for randomly chosen Users containing random DeviceTypes and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateDeviceGroups(int count)
	{
		Random random = new Random();
		List<User> users = userService.getAllUsers().stream().filter(u -> u.getRoles().contains(UserRole.EMPLOYEE)).collect(Collectors.toList());
		List<DeviceType> types = new LinkedList<>(deviceTypeService.getAllDeviceTypes());
		for (int i = 1; i <= count; i++)
		{
			DeviceGroup group = new DeviceGroup();
			
			group.setCreatedBy(users.get(random.nextInt(users.size())));
			List<DeviceType> contained = new LinkedList<>();
			
			int c = random.nextInt(10)+2;
			for (int j = 0; j < c; j++)
			{
				contained.add(types.get(random.nextInt(types.size())));
			}
			
			group.setContainedDevices(contained);
			
			deviceGroupService.saveDeviceGroup(group);
		}
		
		logger.info("> generated " + count + " device groups");
	}
	

	/**
	 * Generates up to count number of {@link DeviceManual}s for each DeviceType and saves them to the DB.
	 * 
	 * @param count
	 */
	private void generateDeviceManuals(int count)
	{
		Random random = new Random();
		for (DeviceType type : deviceTypeService.getAllDeviceTypes())
		{
			int c = random.nextInt(count) + 1;
			for (int i = 1; i <= c; i++)
			{
				DeviceManual manual = new DeviceManual();
				
				manual.setDeviceType(type);
				manual.setFilePath("/manuals/genMan-" + type.getId() + "-" + i);
				
				deviceManualService.saveDeviceManual(manual);
			}
		}
		
		logger.info("> generated up to " + count + " manuals for each type");
	}
	
	/**
	 * Generates count number of {@link Reservation}s created by randomly selected Users, Devices, start- and end-times (with certain patterns).
	 * 
	 * @param count
	 */
	private void generateReservations(int count)
	{
		Random random = new Random();
		Calendar calendar = Calendar.getInstance();
		Date now = new Date();
		List<User> users = new LinkedList<>(userService.getAllUsers());
		List<Device> devices = new LinkedList<>(deviceService.getAllDevices());

		for (int i = 1; i <= count; i++)
		{
			Reservation reservation = new Reservation();
			reservation.setCreatedBy(users.get(random.nextInt(users.size())));
			reservation.setReason("genReservationReason" + i);
			
			Set<Device> devSet = new HashSet<>();
			int devCount = random.nextInt(30) + 1;
			for (int j = 0; j < devCount; j++)
			{
				devSet.add(devices.get(random.nextInt(devices.size())));
			}
			reservation.setDevices(devSet);
			
			if (i < (int) (count * 0.4)) // ended and returned reservations
			{
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, -(random.nextInt(600) + 10));
				reservation.setEndTime(calendar.getTime());
				
				calendar.add(Calendar.MINUTE, -(random.nextInt(9000) + 10));
				reservation.setStartTime(calendar.getTime());
				
				reservation.setReturned(true);
			}
			else if (i < (int) (count * 0.5)) // overdue reservations
			{
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, -(random.nextInt(600) + 10));
				reservation.setEndTime(calendar.getTime());
				
				calendar.add(Calendar.MINUTE, -(random.nextInt(9000) + 10));
				reservation.setStartTime(calendar.getTime());
				
				reservation.setReturned(false);
			}
			else if (i < (int) (count * 0.7)) // currently active reservations
			{
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, random.nextInt(1000) + 10);
				reservation.setEndTime(calendar.getTime());
				
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, -(random.nextInt(4000) + 10));
				reservation.setStartTime(calendar.getTime());
				
				reservation.setReturned(false);
			}
			else // future reservations
			{
				calendar.setTime(now);
				calendar.add(Calendar.MINUTE, random.nextInt(600) + 5);
				reservation.setStartTime(calendar.getTime());
				
				calendar.add(Calendar.MINUTE, random.nextInt(9000) + 10);
				reservation.setEndTime(calendar.getTime());
				
				reservation.setReturned(false);
			}
			
			reservationService.saveReservation(reservation);
		}
		
		logger.info("> generated " + count + " reservations");
	}
}