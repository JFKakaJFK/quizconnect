package at.qe.sepm.skeleton.services;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionType;
import at.qe.sepm.skeleton.repositories.QuestionRepository;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Johannes Koch
 *
 */
@Component
@Scope("application")
public class QuestionService {

	private Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    QuestionRepository questionRepository;

    /**
     * Returns all {@link Question} by {@link QuestionType}
     *
     * @param type
     * @return
     */
    public Collection<Question> getAllByType(QuestionType type){
        return questionRepository.findByType(type);
    }

    /**
     * Returns all {@link Question} where the {@link Question#rightAnswerString} contains the search phrase
     *
     * @param text
     * @return
     */
    public Collection<Question> getAllByAnswerContaining(String text){
        return questionRepository.findAllByAnswersContaining(text);
    }

    /**
     * Returns all {@link Question} where the {@link Question#questionString} contains a search phrase
     *
     * @param text
     * @return
     */
    public Collection<Question> getAllByQuestionContaining(String text){
        return questionRepository.findByQuestionStringContaining(text);
    }

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

        // check wrong answers
        if(question.getWrongAnswerString_1() == null){
            throw new IllegalArgumentException("At least one wrong answer is required");
        }
		if (question.getWrongAnswerString_1() != null && question.getWrongAnswerString_1().length() > 200)
		{
            throw new IllegalArgumentException("Wrong Answer 1 is too long(MAX: 200Chars)");
        }
        if(question.getWrongAnswerString_2() != null && question.getWrongAnswerString_2().length() > 200) {
            throw new IllegalArgumentException("Wrong Answer 2 is too long(MAX: 200Chars)");
        }
        if(question.getWrongAnswerString_3() != null && question.getWrongAnswerString_3().length() > 200) {
            throw new IllegalArgumentException("Wrong Answer 3 is too long(MAX: 200Chars)");
        }
        if(question.getWrongAnswerString_4() != null && question.getWrongAnswerString_4().length() > 200) {
            throw new IllegalArgumentException("Wrong Answer 4 is too long(MAX: 200Chars)");
        }
        if(question.getWrongAnswerString_5() != null && question.getWrongAnswerString_5().length() > 200) {
            throw new IllegalArgumentException("Wrong Answer 5 is too long(MAX: 200Chars)");
        }

        return questionRepository.save(question);
    }

    /**
     * Deletes a {@link Question} from the db
     *
     * @param question
     */
    //@PreAuthorize("principal.username eq question.questionSet.author.user.username")
    public void deleteQuestion(Question question){
        // TODO if type is file, delete all files
        if(question.getType() == QuestionType.picture){
            //...
        }
        questionRepository.delete(question);
        log.info("Deleted Question " + question.getId());
    }

}
