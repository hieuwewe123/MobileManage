package ra.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {
    private int id;
    private int customerId;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;

    public Invoice() {
    }

    public Invoice(int id, int customerId, LocalDateTime createdAt, BigDecimal totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
