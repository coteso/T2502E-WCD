package com.example.javawebmvc.dao;

import com.example.javawebmvc.model.Product;
import com.example.javawebmvc.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) cho bang "products" - cung cap day du 4 thao tac CRUD.
 * Moi phuong thuc tu mo/dong Connection rieng (try-with-resources) de tranh leak ket noi.
 */
public class ProductDAO {

    // ---------- CREATE ----------
    public int insert(Product p) {
        String sql = "INSERT INTO products (id,name, price, quantity, status,deleted,catogoryId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,p.getId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setBoolean(5, p.isStatus());
            ps.setBoolean(6, p.isDeleted());
            ps.setInt(7,p.getCategoryId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them san pham: " + e.getMessage(), e);
        }
    }

    // ---------- READ (all) ----------
    public List<Product> findAll() {
        String sql = "SELECT id, name, price, quantity, status" +
                "FROM products ORDER BY id DESC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach san pham: " + e.getMessage(), e);
        }
        return list;
    }

    // ---------- READ (search theo ten, dung cho o tim kiem) ----------
    public List<Product> search(String keyword) {
        String sql = "SELECT id, name, price, quantity, status " +
                "FROM products WHERE name LIKE ? ORDER BY id DESC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem san pham: " + e.getMessage(), e);
        }
        return list;
    }

    // ---------- READ (by id) ----------
    public Product findById(int id) {
        String sql = "SELECT id, name, price, quantity, status, created_at, updated_at " +
                "FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay san pham theo id: " + e.getMessage(), e);
        }
        return null;
    }

    // ---------- UPDATE ----------
    public boolean update(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setBoolean(4, p.isStatus());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat san pham: " + e.getMessage(), e);
        }
    }

    // ---------- DELETE ----------
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa san pham: " + e.getMessage(), e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getBoolean("status"));

        return p;
    }
}
