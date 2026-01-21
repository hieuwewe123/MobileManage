package ra.presentation;

import ra.business.ICustomerService;
import ra.business.impl.CustomerServiceImpl;
import ra.model.Customer;

import java.util.List;
import java.util.Scanner;

public class CustomerView {

    private final ICustomerService customerService;
    private final Scanner sc = new Scanner(System.in);

    public CustomerView(ICustomerService customerService) {
        this.customerService = customerService;
    }
    public void showCustomerManagement() {
        while (true) {
            System.out.println("\n========== QUẢN LÝ KHÁCH HÀNG ==========");
            System.out.println("1. Hiển thị tất cả khách hàng");
            System.out.println("2. Tìm kiếm khách hàng (tên/sđt/email)");
            System.out.println("3. Thêm khách hàng mới");
            System.out.println("4. Cập nhật khách hàng");
            System.out.println("5. Xóa khách hàng");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidInt();

            switch (choice) {
                case 1 -> displayAllCustomers();
                case 2 -> searchCustomers();
                case 3 -> addNewCustomer();
                case 4 -> updateCustomer();
                case 5 -> deleteCustomer();
                case 0 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void displayAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        printCustomerTable(customers);
    }

    private void searchCustomers() {
        System.out.print("Nhập từ khóa tìm kiếm: ");
        String keyword = sc.nextLine().trim();
        List<Customer> result = customerService.searchCustomers(keyword);
        printCustomerTable(result);
    }

    private void addNewCustomer() {
        Customer c = new Customer();
        System.out.print("Tên khách hàng: ");
        c.setName(sc.nextLine().trim());

        System.out.print("Số điện thoại: ");
        c.setPhone(sc.nextLine().trim());

        System.out.print("Email: ");
        c.setEmail(sc.nextLine().trim());

        System.out.print("Địa chỉ: ");
        c.setAddress(sc.nextLine().trim());

        if (customerService.addCustomer(c)) {
            System.out.println("→ Thêm khách hàng thành công!");
        } else {
            System.out.println("→ Thêm thất bại!");
        }
    }

    private void updateCustomer() {
        System.out.print("Nhập ID khách hàng cần sửa: ");
        int id = getValidInt();
        Customer existing = customerService.findById(id);

        if (existing == null) {
            System.out.println("Không tìm thấy khách hàng!");
            return;
        }

        System.out.println("Thông tin hiện tại:");
        printSingleCustomer(existing);

        Customer updated = new Customer();
        updated.setId(id);

        System.out.print("Tên mới (Enter giữ nguyên): ");
        String name = sc.nextLine().trim();
        updated.setName(name.isEmpty() ? existing.getName() : name);

        System.out.print("SĐT mới (Enter giữ nguyên): ");
        String phone = sc.nextLine().trim();
        updated.setPhone(phone.isEmpty() ? existing.getPhone() : phone);

        System.out.print("Email mới (Enter giữ nguyên): ");
        String email = sc.nextLine().trim();
        updated.setEmail(email.isEmpty() ? existing.getEmail() : email);

        System.out.print("Địa chỉ mới (Enter giữ nguyên): ");
        String address = sc.nextLine().trim();
        updated.setAddress(address.isEmpty() ? existing.getAddress() : address);

        if (customerService.updateCustomer(updated)) {
            System.out.println("→ Cập nhật thành công!");
        } else {
            System.out.println("→ Cập nhật thất bại!");
        }
    }

    private void deleteCustomer() {
        System.out.print("Nhập ID khách hàng muốn xóa: ");
        int id = getValidInt();

        Customer c = customerService.findById(id);
        if (c == null) {
            System.out.println("Không tìm thấy khách hàng!");
            return;
        }

        System.out.println("Bạn có chắc chắn muốn xóa?");
        printSingleCustomer(c);
        System.out.print("Nhập Y để xác nhận, N để hủy: ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if ("Y".equals(confirm)) {
            if (customerService.deleteCustomer(id)) {
                System.out.println("→ Xóa thành công!");
            } else {
                System.out.println("→ Xóa thất bại!");
            }
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void printCustomerTable(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("Không có khách hàng nào!");
            return;
        }
        System.out.println("│ ID  │ Tên khách hàng               │ SĐT         │ Email                  │ Địa chỉ               │");
        for (Customer c : customers) {
            System.out.printf("│ %-3d │ %-28s │ %-11s │ %-22s │ %-21s │\n",
                    c.getId(), truncate(c.getName(), 28),
                    c.getPhone(), truncate(c.getEmail(), 22), truncate(c.getAddress(), 21));
        }
    }

    private void printSingleCustomer(Customer c) {
        System.out.println("ID: " + c.getId());
        System.out.println("Tên: " + c.getName());
        System.out.println("SĐT: " + c.getPhone());
        System.out.println("Email: " + c.getEmail());
        System.out.println("Địa chỉ: " + c.getAddress());
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
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