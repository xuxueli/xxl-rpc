package com.xxl.rpc.example.api.dto;

import java.io.Serializable;

/**
 * User Dto
 */
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 42L;


    private String userName;
    private String word;

    public UserDTO(String userName, String word) {
        this.userName = userName;
        this.word = word;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    @Override
    public String toString() {
        return "UserDTO{" +
                "userName='" + userName + '\'' +
                ", word='" + word + '\'' +
                '}';
    }

}
