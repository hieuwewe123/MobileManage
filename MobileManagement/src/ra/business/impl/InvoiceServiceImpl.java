package ra.business.impl;

import ra.business.IInvoiceService;
import ra.dao.IInvoiceDAO;
import ra.dao.impl.InvoiceDAOImpl;
import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;

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
}
