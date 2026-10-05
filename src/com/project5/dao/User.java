package com.project5.dao;

/** Simple data holder for a row in the `users` table. */
public class User {
    public int id;
    public String userId;
    public String passwordHash;
    public String passwordSalt;
    public String firstName;
    public String lastName;
    public String email;
    public String dateOfBirth; // yyyy-MM-dd
    public String photoPath;   // stored filename under WEB-INF/uploads
    public String securityQuestion;
    public String securityAnswerHash;
    public String securityAnswerSalt;
}
