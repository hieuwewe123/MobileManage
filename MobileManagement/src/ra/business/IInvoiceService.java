package ra.business;

import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInvoiceService {

    int createInvoice(int customerId, BigDecimal totalAmount);

    void addDetail(int invoiceId, int productId, int quantity, BigDecimal unitPrice);

    List<Invoice> getAll();

    List<Invoice> searchByCustomer(String keyword);

    // Thêm cho tìm kiếm theo ngày/tháng/năm
    List<Invoice> searchByDate(String dateStr);

    // Thống kê doanh thu
    List<Map<String, Object>> getRevenueByDay();

    List<Map<String, Object>> getRevenueByMonth();

    List<Map<String, Object>> getRevenueByYear();
}