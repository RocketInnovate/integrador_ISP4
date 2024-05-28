package com.rocketteam.passkeeper.data.model.response;

public class UserResponse {
    private int id;
    private String email;
    private String password;
    private Integer premium;
    private String salt;
    private Integer biometric;

    public UserResponse(int id, String email, String password, Integer premium, String salt, Integer biometric) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.premium = premium;
        this.salt = salt;
        this.biometric = biometric;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer  getPremium(){ return premium;}
    public void setPremium(Integer premium) {
        this.premium = premium;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getBiometric() {
        return biometric;
    }

    public void setBiometric(Integer biometric) {
        this.biometric = biometric;
    }
}
