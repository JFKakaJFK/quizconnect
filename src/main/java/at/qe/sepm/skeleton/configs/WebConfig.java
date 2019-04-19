package at.qe.sepm.skeleton.configs;

import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/unauth.xhtml");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.xhtml");
            container.addErrorPages(error401Page,error404Page);
        };
    }
}
