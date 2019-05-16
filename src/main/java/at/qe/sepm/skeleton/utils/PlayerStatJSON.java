package at.qe.sepm.skeleton.utils;

/**
 * Class for representing {@link at.qe.sepm.skeleton.model.Player} stats as JSON
 */
public class PlayerStatJSON {

    private String[] playedSets; // the index of the set name is the index of its play count
    private int[] setPlayCounts;
    private int[] lastGameScores;

    public PlayerStatJSON(){}

    public PlayerStatJSON(String[] playedSets, int[] setPlayCounts, int[] lastGameScores){
        this.playedSets = playedSets;
        this.setPlayCounts = setPlayCounts;
        this.lastGameScores = lastGameScores;
    }

    public String[] getPlayedSets() {
        return playedSets;
    }

    public void setPlayedSets(String[] playedSets) {
        this.playedSets = playedSets;
    }

    public int[] getSetPlayCounts() {
        return setPlayCounts;
    }

    public void setSetPlayCounts(int[] setPlayCounts) {
        this.setPlayCounts = setPlayCounts;
    }

    public int[] getLastGameScores() {
        return lastGameScores;
    }

    public void setLastGameScores(int[] lastGameScores) {
        this.lastGameScores = lastGameScores;
    }
}
