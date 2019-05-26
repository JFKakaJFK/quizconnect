package at.qe.sepm.skeleton.socket.events;

import java.util.List;

public class RoomInfoEvent extends SocketEvent {

    private int pin;
    private String difficulty;
    private String mode;
    private List<String> questionSets;
    private int score;
    private long alivePingInterval;
    private int numJokers;
    private int num;
    private int state;
    private List<PlayerJSON> players;

    public RoomInfoEvent(){}

    public RoomInfoEvent(int pin,
                         String difficulty,
                         String mode,
                         List<String> questionSets,
                         int score,
                         long alivePingInterval,
                         int numJokers,
                         boolean inLobby,
                         List<PlayerJSON> players){
        this.pin = pin;
        this.difficulty = difficulty;
        this.mode = mode;
        this.questionSets = questionSets;
        this.score = score;
        this.alivePingInterval = alivePingInterval;
        this.numJokers = numJokers;
        this.num = players.size();
        this.players = players;
        // matches client side state constants: 0 = LOBBY & 1 = INGAME
        this.state = inLobby ? 0 : 1;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getQuestionSets() {
        return questionSets;
    }

    public void setQuestionSets(List<String> questionSets) {
        this.questionSets = questionSets;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getAlivePingInterval() {
        return alivePingInterval;
    }

    public void setAlivePingInterval(long alivePingInterval) {
        this.alivePingInterval = alivePingInterval;
    }

    public int getNumJokers() {
        return numJokers;
    }

    public void setNumJokers(int numJokers) {
        this.numJokers = numJokers;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<PlayerJSON> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerJSON> players) {
        this.players = players;
    }
}
