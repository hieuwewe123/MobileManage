package ra.presentation;

import ra.business.IInvoiceService;
import ra.business.impl.InvoiceServiceImpl;
import ra.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InvoiceView {
    private final IInvoiceService invoiceService = new InvoiceServiceImpl();
    private final Scanner sc = new Scanner(System.in);

    public InvoiceView(InvoiceServiceImpl invoiceService) {
    }

    public void showInvoiceManagement() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("   QUẢN LÝ HÓA ĐƠN   ");
            System.out.println("=====================================");
            System.out.println("1. Hiển thị danh sách hóa đơn");
            System.out.println("2. Thêm mới hóa đơn");
            System.out.println("3. Tìm kiếm hóa đơn");
            System.out.println("4. Quay lại menu chính");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 4);

            switch (choice) {
                case 1 -> displayAllInvoices();
                case 2 -> addNewInvoice();
                case 3 -> searchInvoicesMenu();
                case 4 -> { return; }
            }
        }
    }

    private void addNewInvoice() {
        System.out.println("\n--- THÊM MỚI ĐƠN HÀNG ---");
        System.out.print("Nhập ID khách hàng: ");
        int customerId = getValidInt();

        System.out.print("Nhập tổng tiền hóa đơn (VNĐ): ");
        BigDecimal totalAmount = getValidBigDecimal();

        int newInvoiceId = invoiceService.createInvoice(customerId, totalAmount);
        if (newInvoiceId > 0) {
            System.out.println("→ Thêm hóa đơn thành công! ID hóa đơn mới: " + newInvoiceId);
        } else {
            System.out.println("→ Thêm hóa đơn thất bại! Kiểm tra lại ID khách hàng (có thể khách hàng không tồn tại hoặc đã bị xóa).");
        }
    }

    private void displayAllInvoices() {
        List<Invoice> invoices = invoiceService.getAll();
        printInvoiceTable(invoices, "DANH SÁCH TẤT CẢ HÓA ĐƠN");
    }

    private void searchInvoicesMenu() {
        while (true) {
            System.out.println("\n--- TÌM KIẾM HÓA ĐƠN ---");
            System.out.println("1. Theo tên khách hàng");
            System.out.println("2. Theo ngày/tháng/năm (ví dụ: 2025, 2025-01, 2025-01-15)");
            System.out.println("0. Quay lại");
            System.out.print("Nhập lựa chọn: ");

            int subChoice = getValidChoice(0, 2);

            if (subChoice == 0) return;

            List<Invoice> result = null;
            if (subChoice == 1) {
                System.out.print("Nhập tên khách hàng (hoặc từ khóa): ");
                String keyword = sc.nextLine().trim();
                result = invoiceService.searchByCustomer(keyword);
            } else if (subChoice == 2) {
                System.out.print("Nhập ngày/tháng/năm: ");
                String dateStr = sc.nextLine().trim();
                result = invoiceService.searchByDate(dateStr);
            }

            if (result != null) {
                printInvoiceTable(result, "KẾT QUẢ TÌM KIẾM");
            }
        }
    }

    private void statisticRevenue() {
        System.out.println("\n--- THỐNG KÊ DOANH THU ---");

        // Theo ngày
        System.out.println("TỔNG DOANH THU THEO TẤT CẢ CÁC NGÀY:");
        List<Map<String, Object>> byDay = invoiceService.getRevenueByDay();
        printRevenueTable(byDay, "Ngày", "ngay");

        // Theo tháng
        System.out.println("\nTỔNG DOANH THU THEO TẤT CẢ CÁC THÁNG:");
        List<Map<String, Object>> byMonth = invoiceService.getRevenueByMonth();
        printRevenueTable(byMonth, "Tháng", "thang");

        // Theo năm
        System.out.println("\nTỔNG DOANH THU THEO TẤT CẢ CÁC NĂM:");
        List<Map<String, Object>> byYear = invoiceService.getRevenueByYear();
        printRevenueTable(byYear, "Năm", "nam");
    }

    // Hiển thị bảng hóa đơn (định dạng như mô tả)
    private void printInvoiceTable(List<Invoice> invoices, String title) {
        if (invoices.isEmpty()) {
            System.out.println("Không có hóa đơn nào!");
            return;
        }

        System.out.println("\n" + title);
        System.out.println("ID  | Khách hàng                  | Ngày tạo                  | Tổng tiền (VNĐ)");
        System.out.println("----+-----------------------------+---------------------------+-------------------");
        for (Invoice inv : invoices) {
            String customerDisplay = inv.getCustomerName() != null ? inv.getCustomerName() : "ID " + inv.getCustomerId();
            System.out.printf("%-3d | %-27s | %-25s | %,15.0f VNĐ\n",
                    inv.getId(),
                    customerDisplay,
                    inv.getCreatedAt(),
                    inv.getTotalAmount());
        }
    }

    // Hiển thị bảng thống kê doanh thu
    private void printRevenueTable(List<Map<String, Object>> data, String label, String key) {
        if (data.isEmpty()) {
            System.out.println("Chưa có dữ liệu doanh thu.");
            return;
        }

        for (Map<String, Object> row : data) {
            System.out.printf("%s %s: %,15.0f VNĐ\n", label, row.get(key), row.get("tong_doanh_thu"));
        }
    }

    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.printf("Lựa chọn không hợp lệ! Vui lòng nhập số từ %d đến %d: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
            }
        }
    }

    // Hai hàm này được thêm để fix lỗi "cannot find symbol getValidInt()"
    private int getValidInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số nguyên hợp lệ: ");
            }
        }
    }

    private BigDecimal getValidBigDecimal() {
        while (true) {
            try {
                return new BigDecimal(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
            }
        }
    }
}