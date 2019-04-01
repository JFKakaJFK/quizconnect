package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Johannes Koch
 *
 */
@Component
@Scope("application")
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    /**
     * Saves a {@link Question} to the db
     *
     * @param question
     * @return
     * @throws IllegalArgumentException
     */
    public Question saveQuestion(Question question) throws IllegalArgumentException
    {
        // is the context valid?
        if(question == null){
            throw new IllegalArgumentException("Question cannot be null");
        }
        if(question.getQuestionSet() == null){
            throw new IllegalArgumentException("Question must be associated to a QuestionSet");
        }
        if(question.getQuestionSet().isNew()){
            throw new IllegalArgumentException("Save QuestionSet first");
        }

        // is the question itself valid?
        if(question.getType() == null){
            throw new IllegalArgumentException("Question Type can not be null");
        }
        if(question.getQuestionString() == null){
            throw new IllegalArgumentException("Question can not be null");
        }
        if(question.getQuestionString().length() > 200){
            throw new IllegalArgumentException("Question is too long(MAX: 200Chars)");
        }

        if(question.getRightAnswerString() == null){
            throw new IllegalArgumentException("Answer can not be null");
        }
        if(question.getRightAnswerString().length() > 200){
            throw new IllegalArgumentException("Answer is too long(MAX: 200Chars)");
        }
        // TODO more checks? how many wrong answers are mandatory?

        return questionRepository.save(question);
    }

    /**
     * Deletes a {@link Question} from the db
     *
     * @param question
     */
    @PreAuthorize("principal.username eq question.questionSet.author.user.username")
    public void deleteQuestion(Question question){
        // remove question from questionSet before deleting reference
        /* TODO fix cyclic dependency moar abstraction?
        QuestionSet questionSet = question.getQuestionSet();
        questionSet.getQuestions().remove(question);
        QuestionSet savedQuestionSet = ...
        */
        questionRepository.delete(question);
    }

}
