package com.zahariaca.pojo.users;

import com.google.gson.annotations.Expose;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class User implements Comparable<User> {
    @Expose(serialize = false)
    protected final Logger logger = LogManager.getLogger(User.class);
    //TODO: username assumed unique, no mechanism to add users, if added unique username must be guarded for
    @Expose
    String username;
    @Expose
    String userPassword;
    @Expose
    String userId;
    // isSupplier has no usage for now.
    @Expose
    boolean isSupplier;

    public User() {}

    public User(String username, String userPassword, boolean isSupplier) {
        this.username = username;
        this.userPassword = DigestUtils.sha256Hex(userPassword);
        this.userId = DigestUtils.sha256Hex(username);
        this.isSupplier = isSupplier;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSupplier() {
        return isSupplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (isSupplier() != user.isSupplier()) return false;
        if (getUsername() != null ? !getUsername().equals(user.getUsername()) : user.getUsername() != null)
            return false;
        if (getUserPassword() != null ? !getUserPassword().equals(user.getUserPassword()) : user.getUserPassword() != null)
            return false;
        return getUserId() != null ? getUserId().equals(user.getUserId()) : user.getUserId() == null;
    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getUserPassword() != null ? getUserPassword().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (isSupplier() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userId='" + userId + '\'' +
                ", isSupplier=" + isSupplier +
                '}';
    }

    @Override
    public int compareTo(User o) {
        return this.getUserId().compareTo(o.getUserId());
    }
}
