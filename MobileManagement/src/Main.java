
import ra.business.AdminService;
import ra.dao.impl.AdminDAOImpl;
import ra.dao.impl.CustomerDAOImpl;
import ra.dao.impl.ProductDAOImpl;
import ra.business.impl.CustomerServiceImpl;
import ra.business.impl.ProductServiceImpl;
import ra.business.impl.InvoiceServiceImpl;
import ra.presentation.CustomerView;
import ra.presentation.InvoiceView;
import ra.presentation.ProductView;

import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AdminService adminService = new AdminService(new AdminDAOImpl());

    private static final ProductView productView = new ProductView(new ProductServiceImpl(new ProductDAOImpl()));
    private static final CustomerView customerView = new CustomerView(new CustomerServiceImpl(new CustomerDAOImpl()));
    private static final InvoiceView invoiceView = new InvoiceView(new InvoiceServiceImpl());

    public static void main(String[] args) {
        showStartMenu();
    }

    private static void showStartMenu() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("   HỆ THỐNG QUẢN LÝ CỬA HÀNG   ");
            System.out.println("=====================================");
            System.out.println("1. Đăng nhập Admin");
            System.out.println("2. Thoát");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 2);

            if (choice == 1) {
                showAdminLogin();
            } else {
                System.out.println("\nTạm biệt! Hẹn gặp lại.");
                sc.close();
                System.exit(0);
            }
        }
    }

    private static void showAdminLogin() {
        System.out.println("\n=====================================");
        System.out.println("   ĐĂNG NHẬP QUẢN TRỊ   ");
        System.out.println("=====================================");
        System.out.print("Tài khoản: ");
        String username = sc.nextLine().trim();

        System.out.print("Mật khẩu : ");
        String password = sc.nextLine().trim();

        System.out.println("=====================================");

        if (adminService.login(username, password)) {
            System.out.println("Đăng nhập thành công!");
            showMainMenu();
        } else {
            System.out.println("Tài khoản hoặc mật khẩu không đúng!");
        }
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("         MENU CHÍNH         ");
            System.out.println("=====================================");
            System.out.println("1. Quản lý sản phẩm điện thoại");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý hóa đơn");
            System.out.println("4. Thống kê doanh thu");
            System.out.println("5. Đăng xuất");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 5);

            switch (choice) {
                case 1 -> productView.showProductManagement();
                case 2 -> customerView.showCustomerManagement();
                case 3 -> invoiceView.showInvoiceManagement();
                case 4 -> System.out.println("Đang hoàn thiện");
                case 5 -> {
                    System.out.println("\nĐăng xuất thành công!");
                    return;
                }
            }
        }
    }

    private static int getValidChoice(int min, int max) {
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
}