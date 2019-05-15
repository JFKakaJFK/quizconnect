package at.qe.sepm.skeleton.utils;

import java.util.Map;

/**
 * Class for representing {@link at.qe.sepm.skeleton.model.Player} stats as JSON
 */
public class PlayerStatJSON {

    private Map<String, Integer> setPlayCounts;

    public PlayerStatJSON(){}

    public PlayerStatJSON(Map<String, Integer> setPlayCounts){
        this.setPlayCounts = setPlayCounts;
    }

    public Map<String, Integer> getSetPlayCounts() {
        return setPlayCounts;
    }

    public void setSetPlayCounts(Map<String, Integer> setPlayCounts) {
        this.setPlayCounts = setPlayCounts;
    }
}
