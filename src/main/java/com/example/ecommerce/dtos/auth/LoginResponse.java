package com.example.ecommerce.dtos.auth;

import com.example.ecommerce.entities.User;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private User user;

    public LoginResponse(){}
    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken(){return token;}
    public void setToken(String token){this.token=token;}

    public String getType(){return type;}
    public void setType(String type){this.type=type;}

    public User getUser(){return user;}
    public void setUser(User id){this.user =user;}

}
