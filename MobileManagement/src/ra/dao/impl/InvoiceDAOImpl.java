package ra.dao.impl;

import ra.dao.IInvoiceDAO;
import ra.model.Invoice;
import ra.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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