package de.opengamebackend.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private GatewayConfig gatewayConfig;

    @Autowired
    public WebSecurityConfig(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // https://spring.io/guides/topicals/spring-security-architecture/
        http
                // Allow requests from admin panel.
                .cors()
                // Allow POST.
                .and()
                .csrf().disable()
                // Enable authorization.
                .authorizeRequests()
                // Allow login without token.
                .antMatchers("/open-game-backend-auth/login").permitAll()
                // Restrict admin endpoints.
                .antMatchers("**/admin/**").hasRole("ADMIN")
                // Hide server endpoints.
                .antMatchers("**/server/**").hasRole("SERVER")
                // Require token for all other requests.
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), gatewayConfig))
                // Disable session creation on Spring Security.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthLoginResponseFilter authLoginResponseFilter(GatewayConfig gatewayConfig) {
        return new AuthLoginResponseFilter(gatewayConfig);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // Allow requests from any source.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);

        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
