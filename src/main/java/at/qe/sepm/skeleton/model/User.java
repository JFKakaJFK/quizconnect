package at.qe.sepm.skeleton.model;

import java.util.Date;
import java.util.Objects;

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
 * Entity representing users.
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
	
	@OneToOne(optional = true, mappedBy = "user", fetch = FetchType.EAGER)
	private Player player;
	
	@OneToOne(optional = true, mappedBy = "user", fetch = FetchType.EAGER)
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
	
	public Manager getManager()
	{
		return manager;
	}

	public UserRole getRole()
	{
		return role;
	}

	public void setRole(UserRole role)
	{
		this.role = role;
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

    @Override
    public String toString() {
        return "at.qe.sepm.skeleton.model.User[ id=" + username + " ]";
    }

    @Override
    public String getId() {
		return username;
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }

}
