package com.xxl.rpc.sample.server.service.generic;

import java.io.Serializable;

public class User2DTO implements Serializable {
    private static final long serialVersionUID = 42L;

    private String name;
    private String word;

    public User2DTO() {
    }
    public User2DTO(String name, String word) {
        this.name = name;
        this.word = word;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "User2DTO{" +
                "name='" + name + '\'' +
                ", word='" + word + '\'' +
                '}';
    }

}
