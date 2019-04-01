package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Lorenz_Smidt
 *
 */
@Component
@Scope("application")
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public Question saveQuestion(Question question){
        return null;
    }

}
