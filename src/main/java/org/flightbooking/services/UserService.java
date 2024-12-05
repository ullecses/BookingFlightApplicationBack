package org.flightbooking.services;

import jakarta.persistence.EntityManager;
import org.flightbooking.dao.UserDao;
import org.flightbooking.models.Flight;
import org.flightbooking.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Map;

public class UserService {

    private final UserDao userDao;

    public UserService(EntityManager entityManager) {
        this.userDao = new UserDao(entityManager);
    }

    public User createUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userDao.create(user);
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(User user) {
        return userDao.update(user);
    }

    public User updateUserPartial(Long userId, Map<String, Object> updates) {
        return userDao.updatePartial(userId, updates);
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}