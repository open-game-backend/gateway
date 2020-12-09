package de.opengamebackend.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Payload;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    private final GatewayConfig gatewayConfig;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, GatewayConfig gatewayConfig) {
        super(authenticationManager);

        this.gatewayConfig = gatewayConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        // https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/
        String header = req.getHeader(AUTHORIZATION_HEADER_NAME);

        if (header == null || !header.startsWith(AUTHORIZATION_TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER_NAME);

        if (token != null) {
            // Parse the token.
            Payload payload = JWT.require(Algorithm.HMAC512(gatewayConfig.getJwtSecret().getBytes()))
                    .build()
                    .verify(token.replace(AUTHORIZATION_TOKEN_PREFIX, ""));

            String playerId = payload.getSubject();
            List<String> roles = payload.getClaim("roles").asList(String.class);

            List<GrantedAuthority> authorities = new ArrayList<>();

            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            return new UsernamePasswordAuthenticationToken(playerId, null, authorities);
        }

        return null;
    }
}
