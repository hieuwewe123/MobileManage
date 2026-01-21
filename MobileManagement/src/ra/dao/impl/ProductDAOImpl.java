package ra.dao.impl;

import ra.dao.IProductDAO;
import ra.model.Product;
import ra.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements IProductDAO {

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "{call get_all_products()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement call = conn.prepareCall(sql);
             ResultSet rs = call.executeQuery()) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả sản phẩm: " + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "{call search_products_by_keyword(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement call = conn.prepareCall(sql)) {

            call.setString(1, keyword != null ? keyword.trim() : "");
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm: " + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> getByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        String sql = "{call get_products_by_price_range(?, ?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement call = conn.prepareCall(sql)) {

            call.setBigDecimal(1, minPrice);
            call.setBigDecimal(2, maxPrice);
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm theo khoảng giá: " + e.getMessage());
        }
        return products;
    }


    @Override
    public boolean addProduct(Product product) {
        String sql = "CALL add_new_product(?, ?, ?, ?)";
        Connection conn = null;
        CallableStatement call = null;

        try {
            conn = DBUtil.openConnection();
            conn.setAutoCommit(false);

            call = conn.prepareCall(sql);

            call.setString(1, product.getName().trim());
            call.setString(2, product.getBrand().trim());
            call.setBigDecimal(3, product.getPrice());
            call.setInt(4, product.getStock());

            call.execute();

            conn.commit();
            System.out.println("[DEBUG] Thêm sản phẩm committed thành công!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[DEBUG] Rollback do lỗi: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Lỗi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (call != null) call.close();
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    @Override
    public boolean updateProduct(Product product) {
        String sql = "CALL update_product(?, ?, ?, ?, ?)";
        Connection conn = null;
        CallableStatement call = null;

        try {
            conn = DBUtil.openConnection();
            conn.setAutoCommit(false);

            call = conn.prepareCall(sql);

            call.setInt(1, product.getId());
            call.setString(2, product.getName().trim());
            call.setString(3, product.getBrand().trim());
            call.setBigDecimal(4, product.getPrice());
            call.setInt(5, product.getStock());

            call.execute();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Lỗi cập nhật: " + e.getMessage());
            return false;

        } finally {
            try {
                if (call != null) call.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean deleteProduct(int productId) {
        String sql = "CALL delete_product(?)";
        Connection conn = null;
        CallableStatement call = null;

        try {
            conn = DBUtil.openConnection();
            conn.setAutoCommit(false);

            call = conn.prepareCall(sql);

            call.setInt(1, productId);

            call.execute();

            conn.commit();
            System.out.println("[DEBUG] Xóa sản phẩm committed thành công!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Lỗi xóa sản phẩm: " + e.getMessage());
            return false;

        } finally {
            try {
                if (call != null) call.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Product getProductById(int id) {
        String sql = "SELECT * FROM product WHERE id = ?";

        try (Connection conn = DBUtil.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy sản phẩm theo ID: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<Product> searchByNameAndInStock(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "{call search_products_by_name_and_in_stock(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement call = conn.prepareCall(sql)) {

            call.setString(1, keyword != null ? keyword.trim() : "");
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm sản phẩm còn hàng: " + e.getMessage());
        }
        return products;
    }
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getBigDecimal("price"),
                rs.getInt("stock")
        );
    }
}