package at.qe.sepm.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class RandomOnlineTestBean {

    // TODO delete bean
    public boolean getOnline(){
        return Math.random() < 0.5;
    }

    public void setOnline(boolean bool){}
}
