package ra.presentation;

import ra.business.IInvoiceService;
import ra.business.IProductService;
import ra.model.Invoice;
import ra.model.InvoiceDetail;
import ra.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InvoiceView {
    private final IInvoiceService invoiceService;
    private final IProductService productService;
    private final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public InvoiceView(IInvoiceService invoiceService, IProductService productService) {
        this.invoiceService = invoiceService;
        this.productService = productService;
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
                case 4 -> {
                    System.out.println("Quay lại menu chính...");
                    return;
                }
            }
        }
    }

    private void addNewInvoice() {
        System.out.println("\n--- THÊM MỚI HÓA ĐƠN ---");
        System.out.print("Nhập ID khách hàng: ");
        int customerId = getValidInt();

        // Tạo hóa đơn mới với tổng tiền = 0
        int newInvoiceId = invoiceService.createInvoice(customerId);
        if (newInvoiceId <= 0) {
            System.out.println("→ Thêm hóa đơn thất bại! Kiểm tra lại ID khách hàng (có thể khách hàng không tồn tại hoặc đã bị xóa).");
            return;
        }

        System.out.println("→ Tạo hóa đơn thành công! ID hóa đơn: " + newInvoiceId);
        System.out.println("\n--- THÊM SẢN PHẨM VÀO HÓA ĐƠN ---");

        boolean addingProducts = true;
        while (addingProducts) {
            // Hiển thị danh sách sản phẩm còn hàng
            displayProductsInStock();

            System.out.print("\nNhập ID sản phẩm (0 để kết thúc): ");
            int productId = getValidInt();

            if (productId == 0) {
                addingProducts = false;
                continue;
            }

            // Kiểm tra sản phẩm có tồn tại
            Product product = productService.findById(productId);
            if (product == null) {
                System.out.println("→ Không tìm thấy sản phẩm với ID: " + productId);
                continue;
            }

            System.out.println("Sản phẩm: " + product.getName() + " - Giá: " + String.format("%,d", product.getPrice().intValue()) + " VNĐ");
            System.out.println("Tồn kho: " + product.getStock());

            System.out.print("Nhập số lượng: ");
            int quantity = getValidInt();

            if (quantity <= 0) {
                System.out.println("→ Số lượng phải lớn hơn 0!");
                continue;
            }

            // Thêm chi tiết hóa đơn
            boolean success = invoiceService.addDetail(newInvoiceId, productId, quantity);
            if (success) {
                BigDecimal lineTotal = product.getPrice().multiply(new BigDecimal(quantity));
                System.out.println("→ Đã thêm sản phẩm! Thành tiền: " + String.format("%,d", lineTotal.intValue()) + " VNĐ");
            } else {
                System.out.println("→ Thêm sản phẩm thất bại!");
            }

            System.out.print("\nThêm sản phẩm khác? (Y/N): ");
            String continueAdding = sc.nextLine().trim();
            if (!continueAdding.equalsIgnoreCase("Y")) {
                addingProducts = false;
            }
        }

        // Hiển thị chi tiết hóa đơn vừa tạo
        System.out.println("\n=== HÓA ĐƠN ĐÃ TẠO ===");
        displayInvoiceDetailsById(newInvoiceId);
    }

    private void displayProductsInStock() {
        List<Product> products = productService.searchByNameAndInStock("");
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào còn hàng!");
            return;
        }

        System.out.println("\nDANH SÁCH SẢN PHẨM CÒN HÀNG:");
        System.out.println("ID  | Tên sản phẩm              | Giá (VNĐ)        | Tồn kho");
        System.out.println("----+---------------------------+------------------+--------");
        for (Product p : products) {
            System.out.printf("%-3d | %-25s | %,15d | %7d\n",
                    p.getId(),
                    p.getName(),
                    p.getPrice().intValue(),
                    p.getStock());
        }
    }

    private void viewInvoiceDetails() {
        System.out.print("\nNhập ID hóa đơn: ");
        int invoiceId = getValidInt();
        displayInvoiceDetailsById(invoiceId);
    }

    private void displayInvoiceDetailsById(int invoiceId) {
        List<InvoiceDetail> details = invoiceService.getInvoiceDetails(invoiceId);
        if (details.isEmpty()) {
            System.out.println("Không tìm thấy hóa đơn hoặc hóa đơn chưa có sản phẩm!");
            return;
        }

        System.out.println("\nCHI TIẾT HÓA ĐƠN #" + invoiceId);
        System.out.println("STT | Sản phẩm                  | Đơn giá (VNĐ)   | SL | Thành tiền (VNĐ)");
        System.out.println("----+---------------------------+-----------------+----+------------------");

        BigDecimal total = BigDecimal.ZERO;
        int stt = 1;
        for (InvoiceDetail detail : details) {
            BigDecimal lineTotal = detail.getLineTotal();
            total = total.add(lineTotal);
            System.out.printf("%-3d | %-25s | %,15d | %2d | %,16d\n",
                    stt++,
                    detail.getProductName(),
                    detail.getUnitPrice().intValue(),
                    detail.getQuantity(),
                    lineTotal.intValue());
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("TỔNG CỘNG: %,d VNĐ\n", total.intValue());
    }

    private void displayAllInvoices() {
        List<Invoice> invoices = invoiceService.getAll();
        if (invoices.isEmpty()) {
            System.out.println("Chưa có hóa đơn nào!");
            return;
        }

        System.out.println("\nDANH SÁCH HÓA ĐƠN");
        System.out.println("ID  | Khách hàng                  | Ngày tạo           | Tổng tiền (VNĐ)");
        System.out.println("----+-----------------------------+--------------------+-------------------");
        for (Invoice inv : invoices) {
            String customerDisplay = inv.getCustomerName() != null ? inv.getCustomerName() : "ID " + inv.getCustomerId();
            String formattedDate = inv.getCreatedAt().format(dateFormatter);
            System.out.printf("%-3d | %-27s | %-18s | %,15.0f VNĐ\n",
                    inv.getId(),
                    customerDisplay,
                    formattedDate,
                    inv.getTotalAmount());
        }
    }

    private void searchInvoicesMenu() {
        while (true) {
            System.out.println("\n--- TÌM KIẾM HÓA ĐƠN ---");
            System.out.println("1. Tìm theo tên khách hàng");
            System.out.println("2. Tìm theo ngày/tháng/năm");
            System.out.println("3. Quay lại menu hóa đơn");
            System.out.print("Nhập lựa chọn: ");

            int subChoice = getValidChoice(1, 3);

            if (subChoice == 3) return;

            List<Invoice> result = null;
            if (subChoice == 1) {
                System.out.print("Nhập tên khách hàng (hoặc từ khóa): ");
                String keyword = sc.nextLine().trim();
                result = invoiceService.searchByCustomer(keyword);
            } else if (subChoice == 2) {
                System.out.print("Nhập ngày/tháng/năm (dd/MM/yyyy hoặc MM/yyyy hoặc yyyy): ");
                String dateStr = sc.nextLine().trim();
                
                // Convert Vietnamese date format to database format
                dateStr = convertDateFormat(dateStr);
                result = invoiceService.searchByDate(dateStr);
            }

            if (result != null) {
                if (result.isEmpty()) {
                    System.out.println("Không tìm thấy hóa đơn!");
                } else {
                    displaySearchResults(result);
                }
            }
        }
    }

    private void displaySearchResults(List<Invoice> invoices) {
        System.out.println("\nKẾT QUẢ TÌM KIẾM (" + invoices.size() + " hóa đơn)");
        System.out.println("ID  | Khách hàng                  | Ngày tạo           | Tổng tiền (VNĐ)");
        System.out.println("----+-----------------------------+--------------------+-------------------");
        for (Invoice inv : invoices) {
            String customerDisplay = inv.getCustomerName() != null ? inv.getCustomerName() : "ID " + inv.getCustomerId();
            String formattedDate = inv.getCreatedAt().format(dateFormatter);
            System.out.printf("%-3d | %-27s | %-18s | %,15.0f VNĐ\n",
                    inv.getId(),
                    customerDisplay,
                    formattedDate,
                    inv.getTotalAmount());
        }
    }

    private void statisticRevenue() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("   THỐNG KÊ DOANH THU   ");
            System.out.println("=====================================");
            System.out.println("1. Doanh thu theo ngày");
            System.out.println("2. Doanh thu theo tháng");
            System.out.println("3. Doanh thu theo năm");
            System.out.println("4. Quay lại menu chính");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 4);

            switch (choice) {
                case 1 -> showRevenueByDay();
                case 2 -> showRevenueByMonth();
                case 3 -> showRevenueByYear();
                case 4 -> {
                    return;
                }
            }
        }
    }

    private void showRevenueByDay() {
        System.out.println("\n--- DOANH THU THEO NGÀY ---");
        List<Map<String, Object>> byDay = invoiceService.getRevenueByDay();
        printRevenueTable(byDay, "Ngày");
    }

    private void showRevenueByMonth() {
        System.out.println("\n--- DOANH THU THEO THÁNG ---");
        List<Map<String, Object>> byMonth = invoiceService.getRevenueByMonth();
        printRevenueTable(byMonth, "Tháng");
    }

    private void showRevenueByYear() {
        System.out.println("\n--- DOANH THU THEO NĂM ---");
        List<Map<String, Object>> byYear = invoiceService.getRevenueByYear();
        printRevenueTable(byYear, "Năm");
    }

    private void printRevenueTable(List<Map<String, Object>> data, String label) {
        if (data.isEmpty()) {
            System.out.println("Chưa có dữ liệu.");
            return;
        }

        System.out.println(label + "                  | Doanh Thu (VNĐ)");
        System.out.println("-------------------------------------");
        for (Map<String, Object> row : data) {
            System.out.printf("%-20s | %,15.0f\n", row.get(label.toLowerCase()), row.get("tong_doanh_thu"));
        }
    }


    private String convertDateFormat(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return dateStr;
        }

        String[] parts = dateStr.split("/");

        try {
            if (parts.length == 3) {
                // dd/MM/yyyy -> yyyy-MM-dd
                String day = parts[0];
                String month = parts[1];
                String year = parts[2];
                return String.format("%s-%s-%s", year, month, day);
            } else if (parts.length == 2) {
                // MM/yyyy -> yyyy-MM
                String month = parts[0];
                String year = parts[1];
                return String.format("%s-%s", year, month);
            } else if (parts.length == 1) {
                // yyyy -> yyyy
                return parts[0];
            }
        } catch (Exception e) {
            System.out.println("Định dạng ngày không hợp lệ. Vui lòng nhập theo: dd/MM/yyyy, MM/yyyy hoặc yyyy");
        }

        return dateStr; // Return original if conversion fails
    }

    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= min && choice <= max) return choice;
                System.out.printf("Lựa chọn không hợp lệ! Nhập từ %d đến %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
            }
        }
    }

    private int getValidInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số nguyên hợp lệ: ");
            }
        }
    }
}