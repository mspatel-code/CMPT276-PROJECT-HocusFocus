package com.example;

public class adminInfo {

    private String userName;
    private String userType;

    public String getuserName() {
        return this.userName;
    }

    public String getuserType() {
        return this.userType;
    }

    public void setuserName(String un) {
        this.userName = un;
    }

    public void setuserType(String ut) {
        this.userType = ut;
    }

}
