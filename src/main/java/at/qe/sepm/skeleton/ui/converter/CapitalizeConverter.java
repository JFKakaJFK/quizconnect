package at.qe.sepm.skeleton.ui.converter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Scope("request")
public class CapitalizeConverter {

    public String capitalize (String string) {
        return StringUtils.capitalize(string);
    }
}
