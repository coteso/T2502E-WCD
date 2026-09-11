package com.example.javawebmvc.dao;

import com.example.javawebmvc.model.Category;
import com.example.javawebmvc.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // 1. READ ALL (Lấy danh sách tất cả danh mục)
    public List<Category> findAll() {
        String sql = "SELECT id, name, status FROM categories ORDER BY id DESC";
        List<Category> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi khi lay danh sach danh muc: " + e.getMessage(), e);
        }
        return list;
    }

    // 2. CREATE (Thêm mới danh mục - id tự tăng nên không truyền id)
    public boolean insert(Category c) {
        String sql = "INSERT INTO categories (name, status) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setBoolean(2, c.isStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi khi them moi danh muc: " + e.getMessage(), e);
        }
    }

    // 3. UPDATE (Cập nhật thông tin danh mục)
    public boolean update(Category c) {
        String sql = "UPDATE categories SET name = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            ps.setBoolean(2, c.isStatus());
            ps.setInt(3, c.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi khi cap nhat danh muc: " + e.getMessage(), e);
        }
    }

    // 4. DELETE (Xóa danh mục theo ID)
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi khi xoa danh muc: " + e.getMessage(), e);
        }
    }

    // 5. Hàm mapRow (Chuyển đổi dữ liệu từ ResultSet thành đối tượng Category)
    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setStatus(rs.getBoolean("status"));
        return c;
    }
}
