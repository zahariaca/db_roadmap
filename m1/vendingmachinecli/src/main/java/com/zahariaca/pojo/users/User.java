package com.zahariaca.pojo.users;

import com.google.gson.annotations.Expose;
import com.zahariaca.pojo.Product;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class User implements Comparable<User> {
    @Expose(serialize = false)
    protected final Logger logger = LogManager.getLogger(User.class);
    //TODO: username assumed unique, no mechanism to add users
    @Expose
    private static AtomicInteger idGenerator = new AtomicInteger(1);
    @Expose
    String username;
    @Expose
    String userPassword;
    @Expose
    int userId;
    // isSupplier has no usage for now.
    @Expose
    boolean isSupplier;

    public User() {
    }

    public User(String username, String userPassword, boolean isSupplier) {
        this.username = username;
        this.userPassword = userPassword;
        this.userId = idGenerator.incrementAndGet();
        this.isSupplier = isSupplier;
    }

    public User(int userId, String username, String userPassword, boolean isSupplier) {
        this.userId = userId;
        this.username = username;
        this.userPassword = userPassword;
        this.userId = userId;
        this.isSupplier = isSupplier;
    }

    public static void setIdGenerator(AtomicInteger idGenerator) {
        User.idGenerator = idGenerator;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public int getUserId() {
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
        return userId == user.userId &&
                isSupplier == user.isSupplier &&
                Objects.equals(username, user.username) &&
                Objects.equals(userPassword, user.userPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userPassword, userId, isSupplier);
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
        return Integer.compare(this.getUserId(), o.getUserId());
    }
}
