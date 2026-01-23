package ra.presentation;

import ra.business.ICustomerService;
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
            System.out.println("\n=====================================");
            System.out.println("   QUẢN LÝ KHÁCH HÀNG   ");
            System.out.println("=====================================");
            System.out.println("1. Hiển thị danh sách khách hàng");
            System.out.println("2. Thêm khách hàng mới");
            System.out.println("3. Cập nhật thông tin khách hàng");
            System.out.println("4. Xóa khách hàng theo ID");
            System.out.println("5. Quay lại menu chính");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 5);

            switch (choice) {
                case 1 -> displayAllCustomers();
                case 2 -> addNewCustomer();
                case 3 -> updateCustomer();
                case 4 -> deleteCustomer();
                case 5 -> { return; }
            }
        }
    }

    private void displayAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        printCustomerTable(customers, "DANH SÁCH KHÁCH HÀNG");
    }

    private void searchCustomers() {
        System.out.print("Nhập từ khóa tìm kiếm (tên/SĐT/email): ");
        String keyword = sc.nextLine().trim();
        List<Customer> result = customerService.searchCustomers(keyword);
        printCustomerTable(result, "KẾT QUẢ TÌM KIẾM KHÁCH HÀNG");
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
                System.out.println("→ Xóa thất bại! Khách hàng này đã có hóa đơn, không thể xóa để bảo toàn dữ liệu lịch sử.");
            }
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void printCustomerTable(List<Customer> customers, String title) {
        if (customers.isEmpty()) {
            System.out.println("Không có khách hàng nào!");
            return;
        }

        System.out.println("\n" + title);
        System.out.println("┌─────┬───────────────────────────────────────┬───────────────┬────────────────────────────┬───────────────────────┐");
        System.out.println("│ ID  │ Tên khách hàng                        │ SĐT           │ Email                      │ Địa chỉ               │");
        System.out.println("├─────┼───────────────────────────────────────┼───────────────┼────────────────────────────┼───────────────────────┤");

        for (Customer c : customers) {
            System.out.printf("│ %-3d │ %-37s │ %-13s │ %-26s │ %-21s │\n",
                    c.getId(), truncate(c.getName(), 37),
                    c.getPhone(), truncate(c.getEmail() != null ? c.getEmail() : "", 26),
                    truncate(c.getAddress() != null ? c.getAddress() : "", 21));
        }

        System.out.println("└─────┴───────────────────────────────────────┴───────────────┴────────────────────────────┴───────────────────────┘");
    }

    private void printSingleCustomer(Customer c) {
        System.out.println("ID: " + c.getId());
        System.out.println("Tên: " + c.getName());
        System.out.println("SĐT: " + c.getPhone());
        System.out.println("Email: " + (c.getEmail() != null ? c.getEmail() : ""));
        System.out.println("Địa chỉ: " + (c.getAddress() != null ? c.getAddress() : ""));
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
}