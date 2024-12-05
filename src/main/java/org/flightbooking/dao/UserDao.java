package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.User;

import java.util.List;

public class UserDao {

    private final EntityManager entityManager;

    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User create(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    public User findByEmail(String email) {
        return entityManager.find(User.class, email);
    }

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User update(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            User updatedUser = entityManager.merge(user);
            transaction.commit();
            return updatedUser;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public boolean delete(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        return false;
    }
}

