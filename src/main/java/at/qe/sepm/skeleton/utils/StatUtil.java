package at.qe.sepm.skeleton.utils;

import at.qe.sepm.skeleton.model.Player;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Collections;

@Controller
@Scope("request")
public class StatUtil implements Serializable {

    private final long HOUR_MS = 3600000; // = 1000 * 60 * 60
    private final long MINUTE_MS = 60000; // = 1000 * 60

    /**
     * Rounds to 0 decimal places. if the value is greater than 99.5% the value is rounded down.
     *
     * @param val
     * @return
     */
    public String roundPercent(float val){
        if(Float.isNaN(val)){
            return "None";
        }
        return (val >= 0.995 && val != 1.0 ? "99" : round(val * 100.0f, 0) ) + "%";
    }

    public String round(float val, int precision){
        DecimalFormat df;
        if(precision <= 0){
            df = new DecimalFormat("#");
        } else {
            df = new DecimalFormat("#." + String.join("", Collections.nCopies(precision, "#")));
        }
        return df.format(val);
    }

    /**
     * Returns a time in ms as string of format (HH hours mm minutes | textual description if less).
     *
     * @param ms time in Milliseconds
     * @return
     */
    public String msToString(long ms){
        // Converting this to a Date & using a DateFormatter is too much effort for this simple use case,
        // especially since the hours (if > 23) would need to be added manually again etc.
        long hours = ms / HOUR_MS;
        ms -= hours * HOUR_MS;
        long minutes = ms / MINUTE_MS;
        if(hours == 0 && minutes == 0){
            return ms == 0 ? "0" : "< 1min";
        } else if(hours == 0){
            return String.valueOf(minutes) + "min";
        }
        return String.valueOf(hours) + "hrs " + String.valueOf(minutes) + "min";
    }

    /**
     * Returns the number of games the player has played.
     *
     * @param p
     * @return
     */
    public int getGamesPlayed(Player p){
        if(p == null) return 0;
        return p.getqSetPlayCounts().values().stream()
                .reduce(0, Integer::sum);
    }
}
