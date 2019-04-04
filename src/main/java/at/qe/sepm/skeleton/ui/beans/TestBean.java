package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.bean.SessionScoped;

@Controller
@Scope("view")
public class TestBean {

    @Autowired
    private SessionInfoBean sessionInfoBean;

    public String getPlayer(){
        return sessionInfoBean.getCurrentUser().getId();
    }
}
