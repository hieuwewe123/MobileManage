package ra.dao.impl;

import ra.dao.IInvoiceDAO;
import ra.model.Invoice;
import ra.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDAOImpl implements IInvoiceDAO {

    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBigDecimal("total_amount")
        );
        inv.setCustomerName(rs.getString("customer_name"));  // lấy từ join
        return inv;
    }

    // Thống kê doanh thu theo ngày
    public List<Map<String, Object>> getRevenueByDay() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "{call get_revenue_by_day()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ngay", rs.getDate("ngay"));
                row.put("tong_doanh_thu", rs.getBigDecimal("tong_doanh_thu"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê theo ngày: " + e.getMessage());
        }
        return list;
    }

    // Theo tháng
    public List<Map<String, Object>> getRevenueByMonth() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "{call get_revenue_by_month()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("thang", rs.getString("thang"));
                row.put("tong_doanh_thu", rs.getBigDecimal("tong_doanh_thu"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê theo tháng: " + e.getMessage());
        }
        return list;
    }

    // Theo năm
    public List<Map<String, Object>> getRevenueByYear() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "{call get_revenue_by_year()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("nam", rs.getString("nam"));
                row.put("tong_doanh_thu", rs.getBigDecimal("tong_doanh_thu"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê theo năm: " + e.getMessage());
        }
        return list;
    }

    // Tìm kiếm theo ngày/tháng/năm
    public List<Invoice> searchByDate(String dateStr) {
        List<Invoice> list = new ArrayList<>();
        String sql = "{call search_invoices_by_date(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dateStr != null ? dateStr.trim() : "");
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getBigDecimal("total_amount")
                    );
                    inv.setCustomerName(rs.getString("customer_name"));
                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm hóa đơn theo ngày: " + e.getMessage());
        }
        return list;
    }
    @Override
    public int addInvoice(int customerId, BigDecimal totalAmount) {
        String sql = "{ ? = call add_invoice(?, ?) }";
        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, customerId);
            cs.setBigDecimal(3, totalAmount);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Lỗi thêm hóa đơn: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public void addInvoiceDetail(int invoiceId, int productId, int quantity, BigDecimal unitPrice) {
        String sql = "{call add_invoice_detail(?, ?, ?, ?)}";
        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, invoiceId);
            cs.setInt(2, productId);
            cs.setInt(3, quantity);
            cs.setBigDecimal(4, unitPrice);
            cs.execute();
        } catch (SQLException e) {
            System.err.println("Lỗi thêm chi tiết hóa đơn: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "{call get_all_invoices()}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToInvoice(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả hóa đơn: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Invoice> searchByCustomerName(String keyword) {
        List<Invoice> list = new ArrayList<>();
        String sql = "{call search_invoices_by_customer(?)}";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, keyword != null ? keyword.trim() : "");
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm hóa đơn: " + e.getMessage());
        }
        return list;
    }
}