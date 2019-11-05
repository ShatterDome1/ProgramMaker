package com.mpascal.programmaker.db;


import java.util.HashMap;
import java.util.Map;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private Map<String, Object> dbUser;

    public User(String firstName,
                String lastName,
                String email,
                String password,
                String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public User() {
    }

    public void setMap() {
        dbUser = new HashMap<>();
        dbUser.put("dateOfBirth", dateOfBirth);
        dbUser.put("firstName", firstName);
        dbUser.put("lastName", lastName);
        dbUser.put("email", email);
        dbUser.put("password", password);
    }

    public Map<String, Object> getMap() {
        return dbUser;
    }

}
