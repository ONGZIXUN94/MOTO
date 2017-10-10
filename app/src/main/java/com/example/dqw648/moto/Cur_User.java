package com.example.dqw648.moto;

/**
 * Created by DQW648 on 10-Oct-17.
 */

public class Cur_User {
    private String cur_name;
    private String cur_coreid;
    private String cur_identity;

    public Cur_User(String cur_user_name,String cur_user_coreid, String cur_user_identity){
        cur_name = cur_user_name;
        cur_coreid = cur_user_coreid;
        cur_identity = cur_user_identity;
    }

    public String cur_get_name() {
        return cur_name;
    }

    public void cur_setName(String cur_firstName) {
        cur_name = cur_firstName;
    }

    public String cur_get_coreid() {
        return cur_coreid;
    }

    public void cur_setCoreid(String cur_lastName) {
        cur_coreid = cur_lastName;
    }

    public String cur_get_identity() {
        return cur_identity;
    }

    public void cur_setIdentity(String cur_identity) {
        cur_identity = cur_identity;
    }
}