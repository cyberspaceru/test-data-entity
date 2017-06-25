package ru.sbt.qa.tde.entities;

import ru.sbt.qa.tde.core.TestData;

/**
 * Created by cyberspace on 6/25/2017.
 */
@TestData
public class User {

    public String login;
    public String password;
    public boolean rememberMe;
    public Integer age;

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", rememberMe=" + rememberMe +
                ", age=" + age +
                '}';
    }
}
