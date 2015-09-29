package com.phongnt.phong.chattingtostranger.data;

/**
 * Created by phong on 9/27/2015.
 */
public class User {
    private String user;
    private String pass;
    private String email;

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public User(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public User() {
    }
}
