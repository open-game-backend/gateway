package de.opengamebackend.gateway;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JWTConfig {
    private static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    @Value("${de.opengamebackend.gateway.jwtSecret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        if (Strings.isNullOrEmpty(jwtSecret)) {
            throw new IllegalArgumentException("Property 'de.opengamebackend.gateway.jwtSecret' not set.");
        }
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtTokenExpirationTime() {
        return TOKEN_EXPIRATION_TIME;
    }
}
