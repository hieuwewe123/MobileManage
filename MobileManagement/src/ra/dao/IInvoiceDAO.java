package ra.dao;

import ra.model.Invoice;

import java.util.List;

public interface IInvoiceDAO {
    int addInvoice(int customerId, java.math.BigDecimal totalAmount);
    void addInvoiceDetail(int invoiceId, int productId, int quantity, java.math.BigDecimal unitPrice);
    List<Invoice> getAllInvoices();
    List<Invoice> searchByCustomerName(String keyword);
}