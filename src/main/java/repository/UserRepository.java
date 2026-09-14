package repository;

import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username, EntityManager em) {
        if (username == null || username.trim().isEmpty()) return Optional.empty();
        String jpql = "SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.status = true";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("username", username.trim());
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public User save(User user, EntityManager em) {
        em.persist(user);
        return user;
    }
}
