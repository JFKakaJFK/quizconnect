package at.qe.sepm.skeleton.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

@Entity
public class Question implements Persistable<Integer>
{
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne(optional = false)
	private QuestionSet questionSet;
	
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
	
}