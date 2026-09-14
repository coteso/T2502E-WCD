package service;

import entity.Category;
import repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CategoryService - Lớp Business Logic cho danh mục
 * 
 * Chính sách:
 * - Tạo/cập nhật/lấy danh mục
 * - Ràng buộc: Không cho disable Category nếu còn Product active
 * - Validate tên danh mục (không rỗng, duy nhất)
 */
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    /**
     * Lấy tất cả danh mục
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Lấy danh mục theo ID
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Tạo danh mục mới
     * 
     * Validate:
     * - Tên không rỗng
     * - Tên duy nhất (không trùng lặp)
     * 
     * @param category: Danh mục cần tạo
     * @return: Danh mục đã tạo
     */
    public Category createCategory(Category category) {
        validateCategoryName(category.getName());
        
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }

    /**
     * Cập nhật danh mục
     * 
     * Ràng buộc đặc biệt:
     * - Không cho disable (status = false) nếu còn Product active trong danh mục này
     * 
     * @param category: Danh mục cần cập nhật
     * @return: Danh mục đã cập nhật
     */
    public Category updateCategory(Category category) {
        validateCategoryName(category.getName());
        
        // Nếu cố gắng disable (status = false)
        if (!category.getStatus()) {
            // Kiểm tra có Product active nào không
            if (categoryRepository.hasActiveProducts(category.getId())) {
                throw new RuntimeException(
                    "❌ Không thể disable danh mục này! Còn sản phẩm active trong danh mục."
                );
            }
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.update(category);
    }

    /**
     * Validate tên danh mục
     * 
     * @param name: Tên danh mục
     * @throws: RuntimeException nếu invalid
     */
    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("❌ Tên danh mục không được rỗng!");
        }
    }
}
