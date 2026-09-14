package service;

import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repository.UserRepository;
import util.JPAUtil;
import util.PasswordUtil;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public Optional<User> login(String username, String rawPassword) {
        if (username == null || rawPassword == null) return Optional.empty();
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Optional<User> userOpt = userRepository.findByUsername(username, em);
            if (userOpt.isPresent() && PasswordUtil.matches(rawPassword, userOpt.get().getPassword())) {
                return userOpt;
            }
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public void initDefaultAdminAccount() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (userRepository.findByUsername("admin", em).isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(PasswordUtil.hash("admin123"));
                admin.setFullName("Administrator");
                admin.setRole("ADMIN");
                admin.setStatus(true);
                userRepository.save(admin, em);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }
}
