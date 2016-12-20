package com.andre.nfltipapp.model;

/**
 * Created by Andre on 15.12.2016.
 */

public class User {

    public User (String name, String email, String password){
        setUuid("");
        setName(name);
        setEmail(email);
        setPassword(password);
    }

    public User (String name, String password){
        setUuid("");
        setName(name);
        setEmail("");
        setPassword(password);
    }

    private String uuid;
    private String name;
    private String email;
    private String password;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
