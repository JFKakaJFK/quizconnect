package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.ui.beans.ProfileBean;
import at.qe.sepm.skeleton.utils.PlayerStatJSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * This Controller handles
 */
@Controller
@Scope("session")
@RequestMapping
public class PlayerStatsController {

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

    @RequestMapping(value = "/players/stats", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity getStats(){
        if(profileBean.getPlayer() == null){
            return ResponseEntity.badRequest().body(null);
        }
        // TODO save id, name + count since duplicate names are allowed
        Map<String, Integer> setPlayCounts = new HashMap<>();
        Map<Integer, Integer> qSetPlayCounts = profileBean.getPlayer().getqSetPlayCounts();
        for(int key: qSetPlayCounts.keySet()){
            QuestionSet qs = questionSetService.getQuestionSetById(key);
            if(qs == null){
                profileBean.getPlayer().removeQSetFromPlayed(key);
                playerService.savePlayer(profileBean.getPlayer());
            } else {
                setPlayCounts.put(qs.getName(), qSetPlayCounts.get(key));
            }
        }
        return ResponseEntity.ok(new PlayerStatJSON(setPlayCounts));
    }
}
