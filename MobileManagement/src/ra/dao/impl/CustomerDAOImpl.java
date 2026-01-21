package ra.dao.impl;

import ra.dao.ICustomerDAO;
import ra.model.Customer;
import ra.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements ICustomerDAO {

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "{call get_all_customers()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả khách hàng: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Customer getCustomerById(int id) {
        String sql = "{call get_customer_by_id(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy khách hàng theo ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean addCustomer(Customer c) {
        String sql = "CALL add_new_customer(?, ?, ?, ?)";
        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, c.getName());
            cs.setString(2, c.getPhone());
            cs.setString(3, c.getEmail());
            cs.setString(4, c.getAddress());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm khách hàng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateCustomer(Customer c) {
        String sql = "CALL update_customer(?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, c.getId());
            cs.setString(2, c.getName());
            cs.setString(3, c.getPhone());
            cs.setString(4, c.getEmail());
            cs.setString(5, c.getAddress());
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật khách hàng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id) {
        String sql = "CALL delete_customer(?)";
        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa khách hàng: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "{call search_customers(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, keyword != null ? keyword.trim() : "");
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm khách hàng: " + e.getMessage());
        }
        return list;
    }
}