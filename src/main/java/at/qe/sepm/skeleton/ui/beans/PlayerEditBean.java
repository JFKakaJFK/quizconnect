package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class PlayerEditBean {

    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
