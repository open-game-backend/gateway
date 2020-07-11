package de.opengamebackend.gateway;

import java.util.ArrayList;

public class LoginServiceResponse {
    private String playerId;
    private ArrayList<String> roles;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
}
