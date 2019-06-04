package at.qe.sepm.skeleton.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing users. Users must have either an associated Player or Manager. User role gets set automatically upon assigning a Manager or Player to the User.
 */
@Entity
public class User implements Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 100)
    private String username;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    private String password;
	
	private boolean enabled;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Player player;
	
	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Manager manager;

	@Enumerated(EnumType.STRING)
	private UserRole role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
	
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public Date getCreateDate()
	{
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Called automatically upon creation of a new {@link Player}. Should not be called manually!
	 */
	public void setPlayer(Player player)
	{
		if (this.manager != null)
			throw new IllegalArgumentException("Cannot set Player with Manager already set!");
		this.player = player;
		this.role = UserRole.PLAYER;
	}
	
	public Manager getManager()
	{
		return manager;
	}
	
	/**
	 * Called automatically upon creation of a new {@link Manager}. Should not be called manually!
	 */
	public void setManager(Manager manager)
	{
		if (this.player != null)
			throw new IllegalArgumentException("Cannot set Manager with Player already set!");
		this.manager = manager;
		this.role = UserRole.MANAGER;
	}

	public UserRole getRole()
	{
		return role;
	}

	@Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    //@Override
    //public String toString() {
    //    return "at.qe.sepm.skeleton.model.User[ id=" + username + " ]";
    //}

    @Override
    public String getId() {
		return username;
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }

}
