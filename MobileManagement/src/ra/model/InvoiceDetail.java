package ra.model;

import java.math.BigDecimal;

public class InvoiceDetail {
    private int id;
    private int invoiceId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private String productName; // Thêm để hiển thị tên sản phẩm

    public InvoiceDetail() {
    }

    public InvoiceDetail(int id, int invoiceId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Phương thức tính tổng tiền cho dòng này
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}

