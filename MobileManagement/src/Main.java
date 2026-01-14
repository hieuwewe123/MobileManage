import ra.presentation.ProductView;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ProductView view = new ProductView();

        // Menu chính
        while (true) {
            System.out.println("\n=== HỆ THỐNG QUẢN LÝ BÁN ĐIỆN THOẠI ===");
            System.out.println("1. Quản lý sản phẩm");
            System.out.println("2. Quản lý khách hàng (sắp tới)");
            System.out.println("3. Quản lý hóa đơn (sắp tới)");
            System.out.println("0. Thoát");
            System.out.print("Lựa chọn: ");

            int choice = new Scanner(System.in).nextInt();

            if (choice == 1) {
                view.showProductManagement();
            } else if (choice == 0) {
                System.out.println("Tạm biệt!");
                break;
            } else {
                System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}