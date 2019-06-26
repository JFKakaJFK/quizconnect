package at.qe.sepm.skeleton.ui.converter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Bean to capitalize a letter
 */
@Component
@Scope("request")
public class CapitalizeConverter {

    public String capitalize (String string) {
        return StringUtils.capitalize(string);
    }
}
