package at.qe.sepm.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Basic request scoped bean to test bean initialization.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@Component
@Scope("request")
public class SecurityTestBean {

    private boolean showOkDialog = false;
    private String performedAction = "NONE";

    //TODO: JavaDoc for isShowOKDialog
    public boolean isShowOkDialog() {
        return showOkDialog;
    }

    //TODO: JavaDoc for getPerformedAction
    public String getPerformedAction() {
        return performedAction;
    }

    //TODO: JavaDoc for doAdminAction
    @PreAuthorize("hasAuthority('ADMIN')")
    public void doAdminAction() {
        performedAction = "ADMIN";
        showOkDialog = true;
    }

    //TODO: JavaDoc for doManagerAction
    @PreAuthorize("hasAuthority('MANAGER')")
    public void doManagerAction() {
        performedAction = "MANAGER";
        showOkDialog = true;
    }

    //TODO: JavaDoc for doEmployeeAction
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public void doEmployeeAction() {
        performedAction = "EMPLOYEE";
        showOkDialog = true;
    }

    //TODO: JavaDoc for doHideOKDialog
    public void doHideOkDialog() {
        showOkDialog = false;
    }

}
