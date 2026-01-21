
import ra.business.AdminService;
import ra.dao.impl.AdminDAOImpl;
import ra.dao.impl.CustomerDAOImpl;
import ra.dao.impl.ProductDAOImpl;
import ra.business.impl.CustomerServiceImpl;
import ra.business.impl.ProductServiceImpl;
import ra.presentation.CustomerView;
import ra.presentation.InvoiceView;
import ra.presentation.ProductView;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AdminService adminService = new AdminService(new AdminDAOImpl());
    private static final InvoiceView invoiceView = new InvoiceView();
    // Khởi tạo với dependency injection
    private static final ProductView productView = new ProductView(
            new ProductServiceImpl(new ProductDAOImpl())
    );
    private static final CustomerView customerView = new CustomerView(
            new CustomerServiceImpl(new CustomerDAOImpl())
    );

    public static void main(String[] args) {
        if (!login()) {
            System.out.println("Đăng nhập thất bại quá 3 lần. Thoát chương trình.");
            return;
        }

        while (true) {
            System.out.println("\n=====================================");
            System.out.println("   HỆ THỐNG QUẢN LÝ BÁN ĐIỆN THOẠI   ");
            System.out.println("=====================================");
            System.out.println("1. Quản lý sản phẩm");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý hóa đơn (chưa triển khai)");
            System.out.println("0. Thoát chương trình");
            System.out.print("Nhập lựa chọn: ");

            String input = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số hợp lệ!");
                continue;
            }

            switch (choice) {
                case 1 -> productView.showProductManagement();
                case 2 -> customerView.showCustomerManagement();
                case 3 -> invoiceView.showInvoiceManagement();
                case 0 -> {
                    System.out.println("Tạm biệt!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private static boolean login() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        System.out.println("\n=== ĐĂNG NHẬP ADMIN ===");
        while (attempts < MAX_ATTEMPTS) {
            System.out.print("Username: ");
            String username = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            if (adminService.login(username, password)) {
                System.out.println("Đăng nhập thành công!");
                return true;
            }
            attempts++;
            System.out.println("Sai thông tin. Còn " + (MAX_ATTEMPTS - attempts) + " lần thử.");
        }
        return false;
    }
}