package service;

import entity.Category;
import repository.CategoryRepository;

import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id));
    }

    public void saveOrUpdate(Long id, String name, String description, Boolean status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }

        Category category;
        if (id != null) {
            category = getById(id);
            // Disable Category constraint validation
            if (Boolean.FALSE.equals(status) && categoryRepository.countActiveProductsByCategoryId(id) > 0) {
                throw new IllegalStateException("Không thể vô hiệu hóa (disable) danh mục còn chứa sản phẩm đang hoạt động (Active)!");
            }
        } else {
            category = new Category();
        }

        category.setName(name.trim());
        category.setDescription(description);
        category.setStatus(status);

        categoryRepository.save(category);
    }
}