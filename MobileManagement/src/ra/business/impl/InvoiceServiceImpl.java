package ra.business.impl;

import ra.business.IInvoiceService;
import ra.dao.IInvoiceDAO;
import ra.dao.impl.InvoiceDAOImpl;
import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InvoiceServiceImpl implements IInvoiceService {

    private final IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();

    @Override
    public int createInvoice(int customerId, BigDecimal totalAmount) {
        return invoiceDAO.addInvoice(customerId, totalAmount);
    }

    @Override
    public void addDetail(int invoiceId, int productId, int quantity, BigDecimal unitPrice) {
        invoiceDAO.addInvoiceDetail(invoiceId, productId, quantity, unitPrice);
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceDAO.getAllInvoices();
    }

    @Override
    public List<Invoice> searchByCustomer(String keyword) {
        return invoiceDAO.searchByCustomerName(keyword);
    }

    // Thêm phương thức tìm kiếm theo ngày/tháng/năm (cho menu con tìm kiếm)
    public List<Invoice> searchByDate(String dateStr) {
        return invoiceDAO.searchByDate(dateStr);
    }

    // Thống kê doanh thu theo ngày
    public List<Map<String, Object>> getRevenueByDay() {
        return invoiceDAO.getRevenueByDay();
    }

    // Thống kê doanh thu theo tháng
    public List<Map<String, Object>> getRevenueByMonth() {
        return invoiceDAO.getRevenueByMonth();
    }

    // Thống kê doanh thu theo năm
    public List<Map<String, Object>> getRevenueByYear() {
        return invoiceDAO.getRevenueByYear();
    }
}