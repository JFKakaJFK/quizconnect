package at.qe.sepm.skeleton.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;

/**
 * Repository for managing {@link User} entities.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
public interface UserRepository extends AbstractRepository<User, Long> {

    User findFirstByUsername(String username);

    List<User> findByUsernameContaining(String username);

	@Query("SELECT u FROM User u WHERE :role = u.role")
    List<User> findByRole(@Param("role") UserRole role);

}
