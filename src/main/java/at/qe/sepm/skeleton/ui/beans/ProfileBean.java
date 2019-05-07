package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Scope("session")
public class ProfileBean {

    @Autowired
    private PlayerService playerService;

    private Player player;

    public int getId(){
        return 0;
    }

    public void setId(int id) {
        this.player = playerService.getPlayerById(id);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
