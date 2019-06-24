package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.ui.beans.ProfileBean;
import at.qe.sepm.skeleton.utils.PlayerStatJSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

/**
 * This Controller handles requests to a {@link at.qe.sepm.skeleton.model.Player}'s stats.
 */
@Controller
@Scope("session")
@RequestMapping
public class PlayerStatsController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ProfileBean profileBean;
    private QuestionSetService questionSetService;
    private PlayerService playerService;

    @Autowired
    public PlayerStatsController(ProfileBean profileBean, QuestionSetService questionSetService, PlayerService playerService){
        assert profileBean != null;
        assert questionSetService != null;
        assert playerService != null;

        this.profileBean = profileBean;
        this.questionSetService = questionSetService;
        this.playerService = playerService;
    }

    /**
     * This endpoint returns the {@link QuestionSet} play counts and the scores of a
     * {@link at.qe.sepm.skeleton.model.Player}'s last games.
     *
     * @return
     *      Player statistics.
     */
    @RequestMapping(value = "/players/stats", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity getStats(){

        boolean outdated = false;
        Player player = profileBean.getPlayer();
        if(player == null){
            return ResponseEntity.badRequest().body(null);
        }

        Map<Integer, Integer> qSetPlayCounts = new HashMap<>(player.getqSetPlayCounts());
        String[] playedSets = new String[qSetPlayCounts.size()];
        int[] setPlayCounts = new int[qSetPlayCounts.size()];

        try {
            int i = 0; // keeping a second counter probably is better than rewriting the keySet to an Array
            for(int key: qSetPlayCounts.keySet()){
                QuestionSet qs = questionSetService.getQuestionSetById(key);
                if(qs == null){ // remove a set from the played ones if it was deleted
                    player.removeQSetFromPlayed(key);
                    outdated = true;
                } else {
                    playedSets[i] = qs.getName();
                    setPlayCounts[i++] = qSetPlayCounts.get(key);
                }
            }
        } catch (ConcurrentModificationException e){
            log.error("Could not retrieve stats of player " + player.getUser().getUsername());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if(outdated){
            profileBean.setPlayer(playerService.savePlayer(player));
        }
        return ResponseEntity.ok(new PlayerStatJSON(playedSets, setPlayCounts, player.getLastGameScores()));
    }
}
