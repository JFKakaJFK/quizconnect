package at.qe.sepm.skeleton.socket.events;

public class ScoreChangeEvent extends ServerEvent {

    private int newScore;

    public ScoreChangeEvent(){}

    public ScoreChangeEvent(int newScore){
        this.newScore = newScore;
    }

    public int getNewScore() {
        return newScore;
    }

    public void setNewScore(int newScore) {
        this.newScore = newScore;
    }
}
