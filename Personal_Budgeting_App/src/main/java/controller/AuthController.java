package controller;

import database.UserDAO;
import model.User;

public class AuthController {
    private UserDAO userDAO = new UserDAO();
    private static User currentUser;

    public boolean register(String name, String email, String password) {
        if (name == null || email == null || password == null) return false;
        return userDAO.registerUser(name, email, password);
    }

    public boolean login(String email, String password) {
        User user = userDAO.loginUser(email, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean updateProfile(String newName,String newEmail, String newPassword) {
        if (currentUser != null) {
            boolean success = userDAO.updateProfile(currentUser.getUserId(), newName, newEmail, newPassword);
            if (success) {
                currentUser.setName(newName);
            }
            return success;
        }
        return false;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}