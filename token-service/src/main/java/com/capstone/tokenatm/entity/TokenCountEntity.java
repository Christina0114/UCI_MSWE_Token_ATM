package com.capstone.tokenatm.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Tokens")
public class TokenCountEntity {
    @Id
    private Integer user_id;

    private Integer token_count;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getToken_count() {
        return token_count;
    }

    public void setToken_count(Integer token_count) {
        this.token_count = token_count;
    }
}
