package ra.business;

import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;

public interface IInvoiceService {
    int createInvoice(int customerId, BigDecimal totalAmount);
    void addDetail(int invoiceId, int productId, int quantity, BigDecimal unitPrice);
    List<Invoice> getAll();
    List<Invoice> searchByCustomer(String keyword);
}
