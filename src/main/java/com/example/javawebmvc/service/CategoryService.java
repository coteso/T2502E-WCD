package com.example.javawebmvc.service;

import com.example.javawebmvc.dto.CategoryRow;
import com.example.javawebmvc.entity.Category;
import com.example.javawebmvc.exception.BusinessException;
import com.example.javawebmvc.repository.CategoryRepository;
import com.example.javawebmvc.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service cho Category - ràng buộc nghiệp vụ:
 * - Không xoá category nếu còn product (chưa bị xoá mềm).
 * - Không disable category nếu còn product ACTIVE.
 * - Tên category bắt buộc và không trùng.
 */
public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    /** Danh sách category kèm số product active/total - một query duy nhất (chống N+1). */
    public List<CategoryRow> listWithCounts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<CategoryRow> rows = new ArrayList<>();
            for (Object[] row : categoryRepository.findAllWithCounts(em)) {
                Category c = (Category) row[0];
                long total = row[1] == null ? 0L : (Long) row[1];
                long active = row[2] == null ? 0L : (Long) row[2];
                rows.add(new CategoryRow(c.getId(), c.getName(), c.getDescription(),
                        c.isStatus(), active, total));
            }
            return rows;
        } finally {
            em.close();
        }
    }

    public List<Category> listAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return categoryRepository.findAll(em);
        } finally {
            em.close();
        }
    }

    /** Dùng cho dropdown ở form Product - chỉ lấy category đang active. */
    public List<Category> listActive() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Category c WHERE c.status = true ORDER BY c.name", Category.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Category get(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return categoryRepository.findById(em, id)
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + id + ")."));
        } finally {
            em.close();
        }
    }

    public Category create(String name, String description) {
        String normalizedName = requireName(name);
        return JPAUtil.runInTransactionReturn(em -> {
            if (categoryRepository.existsByName(em, normalizedName)) {
                throw new BusinessException("Tên danh mục đã tồn tại: " + normalizedName);
            }
            Category category = new Category(normalizedName, description, true);
            return categoryRepository.save(em, category);
        });
    }

    public Category update(Long id, String name, String description) {
        String normalizedName = requireName(name);
        return JPAUtil.runInTransactionReturn(em -> {
            Category category = categoryRepository.findById(em, id)
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + id + ")."));
            if (categoryRepository.existsByNameAndIdNot(em, normalizedName, id)) {
                throw new BusinessException("Tên danh mục đã tồn tại: " + normalizedName);
            }
            category.setName(normalizedName);
            category.setDescription(description);
            return categoryRepository.save(em, category);
        });
    }

    /**
     * Bật/tắt trạng thái danh mục.
     * Chỉ cho DISABLE nếu không còn Product active thuộc category đó (mục 7 của đề).
     */
    public Category setStatus(Long id, boolean status) {
        return JPAUtil.runInTransactionReturn(em -> {
            Category category = categoryRepository.findById(em, id)
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + id + ")."));

            if (!status) {
                long activeProducts = categoryRepository.countProducts(em, id, true);
                if (activeProducts > 0) {
                    throw new BusinessException("Không thể vô hiệu hoá danh mục '" + category.getName()
                            + "' vì còn " + activeProducts + " sản phẩm đang hoạt động.");
                }
            }
            category.setStatus(status);
            return categoryRepository.save(em, category);
        });
    }

    /**
     * Xoá cứng category - CHỈ khi không còn product nào (kể cả đã xoá mềm)
     * để không phá khoá ngoại category_id của Product.
     */
    public void delete(Long id) {
        JPAUtil.runInTransaction(em -> {
            Category category = categoryRepository.findById(em, id)
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + id + ")."));

            long totalProducts = categoryRepository.countProducts(em, id, false);
            if (totalProducts > 0) {
                throw new BusinessException("Không thể xoá danh mục '" + category.getName()
                        + "' vì còn " + totalProducts + " sản phẩm đang tham chiếu.");
            }

            Category managed = em.contains(category) ? category : em.merge(category);
            em.remove(managed);
        });
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Tên danh mục không được để trống.");
        }
        return name.trim();
    }

    /** Trả về Optional để servlet hiển thị form sửa category nếu cần. */
    public Optional<Category> find(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return categoryRepository.findById(em, id);
        } finally {
            em.close();
        }
    }
}
