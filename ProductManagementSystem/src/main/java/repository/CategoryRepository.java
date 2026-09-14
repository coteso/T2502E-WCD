package repository;

import entity.Category;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private EntityManager em;

    public CategoryRepository(EntityManager em) {
        this.em = em;
    }

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }
}