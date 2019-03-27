package at.qe.sepm.skeleton.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.data.domain.Persistable;

@Entity
public class Question implements Persistable<Integer>
{
	
	@ManyToOne(optional = false)
	private QuestionSet questionSet;
}