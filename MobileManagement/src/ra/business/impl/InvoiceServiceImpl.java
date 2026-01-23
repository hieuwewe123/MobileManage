package ra.business.impl;

import ra.business.IInvoiceService;
import ra.business.IProductService;
import ra.dao.IInvoiceDAO;
import ra.dao.impl.InvoiceDAOImpl;
import ra.model.Invoice;
import ra.model.InvoiceDetail;
import ra.model.Product;

import java.util.List;
import java.util.Map;

public class InvoiceServiceImpl implements IInvoiceService {

    private final IInvoiceDAO invoiceDAO = new InvoiceDAOImpl();
    private final IProductService productService;

    public InvoiceServiceImpl(IProductService productService) {
        this.productService = productService;
    }

    @Override
    public int createInvoice(int customerId) {
        return invoiceDAO.addInvoice(customerId);
    }

    @Override
    public boolean addDetail(int invoiceId, int productId, int quantity) {
        // Lấy thông tin sản phẩm để lấy giá
        Product product = productService.findById(productId);
        if (product == null) {
            System.err.println("Không tìm thấy sản phẩm với ID: " + productId);
            return false;
        }
        
        // Kiểm tra tồn kho
        if (product.getStock() < quantity) {
            System.err.println("Không đủ hàng trong kho! Tồn kho hiện tại: " + product.getStock());
            return false;
        }

        // Thêm chi tiết hóa đơn với đơn giá lấy từ sản phẩm
        return invoiceDAO.addInvoiceDetail(invoiceId, productId, quantity, product.getPrice());
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceDAO.getAllInvoices();
    }

    @Override
    public List<Invoice> searchByCustomer(String keyword) {
        return invoiceDAO.searchByCustomerName(keyword);
    }

    @Override
    public List<InvoiceDetail> getInvoiceDetails(int invoiceId) {
        return invoiceDAO.getInvoiceDetails(invoiceId);
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