package com.capstone.tokenatm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Tokens")
public class TokenCountEntity {
    @Id
    private Integer user_id;

    private Integer token_count;

    private String user_name;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    private String user_email;

    private Date timestamp;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Integer getToken_count() {
        return token_count;
    }


    public void setTimestamp(Date current_time) {
        this.timestamp = current_time;
    }

    public void setToken_count(Integer token_count) {
        this.token_count = token_count;
    }
}
