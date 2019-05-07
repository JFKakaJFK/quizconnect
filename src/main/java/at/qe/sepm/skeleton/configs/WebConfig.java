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
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401.xhtml");
            ErrorPage error402Page = new ErrorPage(HttpStatus.PAYMENT_REQUIRED, "/error/402.xhtml");
            ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error/403.xhtml");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404.xhtml");
            ErrorPage error418Page = new ErrorPage(HttpStatus.I_AM_A_TEAPOT, "/error/418.xhtml");
            ErrorPage error451Page = new ErrorPage(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, "/error/451.xhtml");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500.xhtml");
            container.addErrorPages(error401Page,
                    error402Page,
                    error403Page,
                    error404Page,
                    error418Page,
                    error451Page,
                    error500Page);
        };
    }
}
