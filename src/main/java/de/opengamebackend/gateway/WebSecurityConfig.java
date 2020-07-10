package de.opengamebackend.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JWTConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // https://spring.io/guides/topicals/spring-security-architecture/
        http
                // Allow POST.
                .csrf().disable()
                // Enable authorization.
                .authorizeRequests()
                // Allow register and login without token.
                .antMatchers("/open-game-backend-auth/register", "/open-game-backend-auth/login").permitAll()
                // Require token for all other requests.
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtConfig))
                // Disable session creation on Spring Security.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthLoginResponseFilter authLoginResponseFilter() {
        return new AuthLoginResponseFilter();
    }

    @Bean
    public JWTConfig jwtConfig() { return new JWTConfig(); }
}
