package at.qe.sepm.skeleton.socket.events;

/**
 * The Class responsible for unmarshalling incoming JSON to a java class.
 */
public class ClientEvent {

    private String event;
    private int playerId;
    private int questionId;
    private int answerId;
    private String message;


    public ClientEvent(){}

    public String getEvent() {
        return event;
    }

    public void setEvent(String type) {
        this.event = type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
