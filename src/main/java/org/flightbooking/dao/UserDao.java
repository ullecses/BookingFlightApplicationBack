package org.flightbooking.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.flightbooking.models.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing User entities.
 * Provides CRUD operations and partial update functionality for users.
 */
public class UserDao {

    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());
    private final EntityManager entityManager;

    /**
     * Constructs a UserDao with the given EntityManager.
     *
     * @param entityManager the EntityManager to manage persistence.
     */
    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new user in the database.
     *
     * @param user the User entity to persist.
     * @return the persisted User entity.
     */
    public User create(User user) {
        LOGGER.info("Creating new user: " + user);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            LOGGER.info("User created successfully: " + user.getId());
            return user;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error creating user: " + user, e);
            throw e;
        }
    }

    /**
     * Finds a user by its ID.
     *
     * @param id the ID of the user.
     * @return the User entity if found, or null if not found.
     */
    public User findById(Long id) {
        LOGGER.info("Finding user by ID: " + id);
        return entityManager.find(User.class, id);
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user.
     * @return the User entity if found, or null if not found.
     */
    public User findByEmail(String email) {
        LOGGER.info("Finding user by email: " + email);
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all User entities.
     */
    public List<User> findAll() {
        LOGGER.info("Retrieving all users from the database...");
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the User entity with updated data.
     * @return the updated User entity.
     */
    public User update(User user) {
        LOGGER.info("Updating user: " + user.getId());
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            User updatedUser = entityManager.merge(user);
            transaction.commit();
            LOGGER.info("User updated successfully: " + user.getId());
            return updatedUser;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getId(), e);
            throw e;
        }
    }

    /**
     * Partially updates a user in the database.
     *
     * @param userId  the ID of the user to update.
     * @param updates a map of field names and their new values.
     * @return the updated User entity, or null if not found.
     */
    public User updatePartial(Long userId, Map<String, Object> updates) {
        LOGGER.info("Partially updating user: " + userId);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            User user = entityManager.find(User.class, userId);
            if (user == null) {
                LOGGER.warning("User not found for partial update: " + userId);
                transaction.rollback();
                return null;
            }

            updates.forEach((key, value) -> {
                try {
                    if (!"id".equalsIgnoreCase(key)) {
                        Field field = User.class.getDeclaredField(key);
                        field.setAccessible(true);

                        if (field.getType().equals(LocalDateTime.class)) {
                            field.set(user, LocalDateTime.parse(value.toString()));
                        } else {
                            field.set(user, value);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    LOGGER.warning("Unknown field in partial update: " + key);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LOGGER.warning("Failed to update field: " + key + " due to " + e.getMessage());
                }
            });

            User updatedUser = entityManager.merge(user);
            transaction.commit();
            LOGGER.info("User partially updated successfully: " + userId);
            return updatedUser;
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error partially updating user: " + userId, e);
            throw e;
        }
    }

    /**
     * Deletes a user from the database by its ID.
     *
     * @param id the ID of the user to delete.
     * @return true if the user was deleted, false otherwise.
     */
    public boolean delete(Long id) {
        LOGGER.info("Deleting user with ID: " + id);
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            User user = entityManager.find(User.class, id);
            if (user != null) {
                entityManager.remove(user);
                LOGGER.info("User deleted successfully: " + id);
                transaction.commit();
                return true;
            } else {
                LOGGER.warning("User not found for deletion: " + id);
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw e;
        }
    }
}