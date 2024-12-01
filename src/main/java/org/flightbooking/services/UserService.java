package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.UserDao;
import org.flightbooking.models.User;

import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService(EntityManager entityManager) {
        this.userDao = new UserDao(entityManager);
    }

    public User createUser(String firstName, String lastName, String email, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        return userDao.create(user);
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(User user) {
        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}