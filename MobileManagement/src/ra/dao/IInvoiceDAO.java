package ra.dao;

import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;

public interface IInvoiceDAO {
    int addInvoice(int customerId, BigDecimal totalAmount);
    void addInvoiceDetail(int invoiceId, int productId, int quantity, BigDecimal unitPrice);
    List<Invoice> getAllInvoices();
    List<Invoice> searchByCustomerName(String keyword);
    List<Invoice> searchByDate(String dateStr);
    List<java.util.Map<String, Object>> getRevenueByDay();
    List<java.util.Map<String, Object>> getRevenueByMonth();
    List<java.util.Map<String, Object>> getRevenueByYear();
}