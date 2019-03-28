package at.qe.sepm.skeleton.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.domain.Persistable;

/**
 * Entity representing a Manager.
 * 
 * @author Lorenz_Smidt
 *
 */
@Entity
public class Manager implements Persistable<Integer>
{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToOne(optional = false, fetch = FetchType.EAGER)
	private User user;
	
	@Column(length = 100)
	private String email;
	
	@Column(length = 200)
	private String institution;
	
	@OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private Set<Player> createdPlayers;
	
	@OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<QuestionSet> createdQuestionSets;
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		this.user = user;
		user.setRole(UserRole.MANAGER);
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String getInstitution()
	{
		return institution;
	}
	
	public void setInstitution(String institution)
	{
		this.institution = institution;
	}
	
	public Set<Player> getCreatedPlayers()
	{
		return createdPlayers;
	}
	
	public void setCreatedPlayers(Set<Player> createdPlayers)
	{
		this.createdPlayers = createdPlayers;
	}
	
	public Set<QuestionSet> getCreatedQuestionSets()
	{
		return createdQuestionSets;
	}
	
	public void setCreatedQuestionSets(Set<QuestionSet> createdQuestionSets)
	{
		this.createdQuestionSets = createdQuestionSets;
	}
	
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	@Override
	public Integer getId()
	{
		return id;
	}
	
	@Override
	public boolean isNew()
	{
		return id == null;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Manager other = (Manager) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}