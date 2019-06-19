package at.qe.sepm.skeleton.socket.events;

/**
 * Class for JSON representation of an Answer
 */
public class AnswerJSON {

    private int answerId;
    private String answer;
    private int playerId;

    public AnswerJSON(){}

    public AnswerJSON(int id, String answer, int playerId){
        this.answerId = id;
        this.answer = answer;
        this.playerId = playerId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int id) {
        this.answerId = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
