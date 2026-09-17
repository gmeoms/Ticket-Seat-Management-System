package com.eventticket.service;

import com.eventticket.model.User;
import com.eventticket.util.DataStore;
import com.eventticket.util.InputValidator;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final DataStore dataStore;
    private User currentUser;

    public UserService(DataStore dataStore) {
        this.dataStore = dataStore;
        this.currentUser = null;
    }

    public User registerUser(String username, String password, String fullName, String email, User.Role role) {
        if (!InputValidator.isValidUsername(username)) {
            System.out.println("[Error] Invalid username format! Must be 3-20 alphanumeric characters.");
            return null;
        }
        if (!InputValidator.isValidPassword(password)) {
            System.out.println("[Error] Password must be at least 4 characters.");
            return null;
        }
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("[Error] Invalid email address format!");
            return null;
        }

        for (User u : dataStore.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("[Error] Username '" + username + "' is already taken.");
                return null;
            }
        }

        String userId = "U" + (dataStore.getUsers().size() + 101);
        User newUser = new User(userId, username, password, fullName, email, role);
        dataStore.getUsers().add(newUser);
        dataStore.save();

        System.out.println("[Success] Registration successful! Account created for " + fullName + " (" + role + ").");
        return newUser;
    }

    public User login(String username, String password) {
        for (User u : dataStore.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                this.currentUser = u;
                System.out.println("[Success] Welcome back, " + u.getFullName() + "! logged in as " + u.getRole() + ".");
                return u;
            }
        }
        System.out.println("[Error] Invalid username or password!");
        return null;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("[Info] User " + currentUser.getUsername() + " logged out.");
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    public List<User> getAllUsers() {
        return dataStore.getUsers();
    }
}
