package ra.presentation;

import ra.business.IInvoiceService;
import ra.business.impl.InvoiceServiceImpl;
import ra.model.Invoice;

import java.util.List;
import java.util.Scanner;

public class InvoiceView {
    private final IInvoiceService invoiceService = new InvoiceServiceImpl();
    private final Scanner sc = new Scanner(System.in);

    public void showInvoiceManagement() {
        while (true) {
            System.out.println("\n========== QUẢN LÝ HÓA ĐƠN ==========");
            System.out.println("1. Hiển thị tất cả hóa đơn");
            System.out.println("2. Tìm kiếm hóa đơn theo tên khách hàng");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidInt();

            switch (choice) {
                case 1 -> displayAllInvoices();
                case 2 -> searchInvoices();
                case 0 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void displayAllInvoices() {
        List<Invoice> invoices = invoiceService.getAll();
        if (invoices.isEmpty()) {
            System.out.println("Chưa có hóa đơn nào!");
            return;
        }
        System.out.println("ID  | Khách hàng          | Ngày tạo                  | Tổng tiền         ");
        System.out.println("----|---------------------|---------------------------|-------------------");
        for (Invoice inv : invoices) {
            System.out.printf("%-3d | %-19s | %-25s | %,15.0f VNĐ\n",
                    inv.getId(),
                    inv.getCustomerName() != null ? inv.getCustomerName() : "ID " + inv.getCustomerId(),
                    inv.getCreatedAt(),
                    inv.getTotalAmount());
        }
    }

    private void searchInvoices() {
        System.out.print("Nhập tên khách hàng: ");
        String keyword = sc.nextLine().trim();
        List<Invoice> result = invoiceService.searchByCustomer(keyword);
        if (result.isEmpty()) {
            System.out.println("Không tìm thấy hóa đơn!");
            return;
        }
        displayInvoices(result);  // dùng chung hàm hiển thị
    }

    private void displayInvoices(List<Invoice> invoices) {
        System.out.println("ID  | Khách hàng          | Ngày tạo                  | Tổng tiền         ");
        System.out.println("----|---------------------|---------------------------|-------------------");
        for (Invoice inv : invoices) {
            System.out.printf("%-3d | %-19s | %-25s | %,15.0f VNĐ\n",
                    inv.getId(),
                    inv.getCustomerName() != null ? inv.getCustomerName() : "ID " + inv.getCustomerId(),
                    inv.getCreatedAt(),
                    inv.getTotalAmount());
        }
    }

    private int getValidInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
            }
        }
    }
}