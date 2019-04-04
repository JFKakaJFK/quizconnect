package at.qe.sepm.skeleton.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring configuration for web security.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Softwaredevelopment and Project Management" offered by the University
 * of Innsbruck.
 */
@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // TODO check why these 2 are set, if only for H2, remove
        http.csrf().disable();
        http.headers().frameOptions().disable(); // needed for H2 console

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login.xhtml");

        http.authorizeRequests()
                .antMatchers("/error/**")
                .permitAll()
                //Permit access only for some roles
                .antMatchers("/secured/**")
				.hasAnyAuthority("MANAGER")
                .antMatchers("/player/**")
                .hasAnyAuthority("PLAYER")
                .antMatchers("/players/**")
                .hasAnyAuthority("PLAYER", "MANAGER")
                .antMatchers("/login/**")
                .hasAnyAuthority("PLAYER", "MANAGER")
                //If user doesn't have permission, forward him to login page
                .and()
                .formLogin()
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/login/redirect.xhtml");
        // :TODO: user failureUrl(/login.xhtml?error) and make sure that a corresponding message is displayed

        http.exceptionHandling().accessDeniedPage("/error/denied.xhtml");

        http.sessionManagement().invalidSessionUrl("/");

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //Configure roles and passwords via datasource
        auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username, password, enabled from user where username=?")
				.authoritiesByUsernameQuery("select username, role from user where username=?").passwordEncoder(new BCryptPasswordEncoder(6));
    }

}
