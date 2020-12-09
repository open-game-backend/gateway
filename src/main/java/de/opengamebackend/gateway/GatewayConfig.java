package de.opengamebackend.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConstructorBinding
@ConfigurationProperties("de.opengamebackend.gateway")
@Validated
public class GatewayConfig {
    private static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    @NotNull
    private String jwtSecret;

    public GatewayConfig(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtTokenExpirationTime() {
        return TOKEN_EXPIRATION_TIME;
    }
}
