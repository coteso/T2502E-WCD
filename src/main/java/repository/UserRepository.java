package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import entity.User;
import util.JPAUtil;

/**
 * UserRepository - Data access layer cho User entity
 * 
 * Sử dụng JPA EntityManager thay vì JDBC trực tiếp
 * Cung cấp phương thức tìm kiếm và CRUD cho User
 */
public class UserRepository {

    /**
     * Tìm user theo username
     * @param username tên đăng nhập
     * @return User object nếu tìm thấy, null nếu không tìm thấy
     */
    public User findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class
            );
            query.setParameter("username", username);
            
            try {
                return query.getSingleResult();
            } catch (jakarta.persistence.NoResultException e) {
                // User không tồn tại
                return null;
            }
        } finally {
            em.close();
        }
    }

    /**
     * Tìm user theo ID
     * @param id user ID
     * @return User object nếu tìm thấy, null nếu không tìm thấy
     */
    public User findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }
}
