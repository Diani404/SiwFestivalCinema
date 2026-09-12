package it.uniroma3.siw.festivalcinema.authentication;

import static it.uniroma3.siw.festivalcinema.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.festivalcinema.model.Credentials.USER_ROLE;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
            "SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
        manager.setAuthoritiesByUsernameQuery(
            "SELECT username, role FROM credentials WHERE username=?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {

        RequestMatcher apiRequests = request -> request.getRequestURI().startsWith("/api/");

        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/403",
                    "/css/**", "/images/**", "/fonts/**", "/uploads/**", "/react/**", "/favicon.ico").permitAll();
            authorize.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll();
            authorize.requestMatchers("/movies/*/reviews/**").hasAnyAuthority(USER_ROLE, ADMIN_ROLE);
            authorize.requestMatchers("/reviews/**").hasAnyAuthority(USER_ROLE, ADMIN_ROLE);
            authorize.requestMatchers(HttpMethod.GET, "/festivals/**", "/movies/**", "/directors/**").permitAll();
            authorize.requestMatchers(HttpMethod.GET, "/api/**").permitAll();
            authorize.requestMatchers("/admin/**").hasAnyAuthority(ADMIN_ROLE);
            authorize.anyRequest().authenticated();
        });

        httpSecurity.formLogin(form -> {
            form.loginPage("/login").permitAll();
            form.defaultSuccessUrl("/", true);
            form.failureUrl("/login?error=true");
        });

        httpSecurity.logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/");
            logout.invalidateHttpSession(true);
            logout.deleteCookies("JSESSIONID");
            logout.clearAuthentication(true);
            logout.permitAll();
        });

        //401
        httpSecurity.exceptionHandling(exceptions -> {
            exceptions.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), apiRequests);
            exceptions.accessDeniedPage("/403");
        });

        //non invia il token CSRF
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return httpSecurity.build();
    }
}
