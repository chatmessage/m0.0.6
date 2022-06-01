package ru.chatmessage.chat.data;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class UserData {

    @SerializedName("login")
    private String login;

    @SerializedName("token")
    private String token;

    public UserData(String login, String token) {
        this.login = login;
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(login, userData.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
