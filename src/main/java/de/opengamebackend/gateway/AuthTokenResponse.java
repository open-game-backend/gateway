package de.opengamebackend.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AuthTokenResponse {
    private String token;
    private String provider;
    private String providerUserId;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean locked;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean firstTimeSetup;

    public AuthTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isFirstTimeSetup() {
        return firstTimeSetup;
    }

    public void setFirstTimeSetup(boolean firstTimeSetup) {
        this.firstTimeSetup = firstTimeSetup;
    }
}
