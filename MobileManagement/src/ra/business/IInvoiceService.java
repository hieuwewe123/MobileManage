package ra.business;

import ra.model.Invoice;
import ra.model.InvoiceDetail;

import java.util.List;
import java.util.Map;

public interface IInvoiceService {

    int createInvoice(int customerId);

    boolean addDetail(int invoiceId, int productId, int quantity);

    List<Invoice> getAll();

    List<Invoice> searchByCustomer(String keyword);
    
    List<InvoiceDetail> getInvoiceDetails(int invoiceId);
    
    // Thêm cho tìm kiếm theo ngày/tháng/năm
    List<Invoice> searchByDate(String dateStr);

    // Thống kê doanh thu
    List<Map<String, Object>> getRevenueByDay();

    List<Map<String, Object>> getRevenueByMonth();

    List<Map<String, Object>> getRevenueByYear();
}