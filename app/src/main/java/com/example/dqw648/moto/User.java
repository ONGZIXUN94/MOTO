package com.example.dqw648.moto;

/**
 * Created by DQW648 on 13-Sep-17.
 */

public class User {
    private String name;
    private String coreid;
    private String identity;

    public User(String user_name,String user_coreid, String user_identity){
        name = user_name;
        coreid = user_coreid;
        identity = user_identity;
    }

    public String get_name() {
        return name;
    }

    public void setName(String firstName) {
        name = firstName;
    }

    public String get_coreid() {
        return coreid;
    }

    public void setCoreid(String lastName) {
        coreid = lastName;
    }

    public String get_identity() {
        return identity;
    }

    public void setIdentity(String favFood) {
        identity = favFood;
    }
}
