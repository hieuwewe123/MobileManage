package ra.presentation;

import ra.business.IProductService;
import ra.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ProductView {

    private final IProductService productService;
    private final Scanner sc = new Scanner(System.in);

    public ProductView(IProductService productService) {
        this.productService = productService;
    }

    public void showProductManagement() {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("   QUẢN LÝ SẢN PHẨM ĐIỆN THOẠI   ");
            System.out.println("=====================================");
            System.out.println("1. Hiển thị danh sách sản phẩm");
            System.out.println("2. Thêm sản phẩm mới");
            System.out.println("3. Cập nhật thông tin sản phẩm");
            System.out.println("4. Xóa sản phẩm theo ID");
            System.out.println("5. Tìm kiếm theo khoảng giá");
            System.out.println("6. Tìm kiếm theo Brand");
            System.out.println("7. Tìm kiếm theo tồn kho");
            System.out.println("8. Quay lại menu chính");
            System.out.println("=====================================");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidChoice(1, 8);

            switch (choice) {
                case 1 -> displayAllProducts();
                case 2 -> addNewProduct();
                case 3 -> updateProduct();
                case 4 -> deleteProduct();
                case 5 -> searchByPriceRange();
                case 6 -> searchProducts();
                case 7 -> searchProductsInStock();
                case 8 -> { return; }
            }
        }
    }

    private void displayAllProducts() {
        List<Product> products = productService.getAllProducts();
        printProductTable(products, "DANH SÁCH SẢN PHẨM");
    }

    private void searchProducts() {
        System.out.print("Nhập từ khóa (tên hoặc brand): ");
        String keyword = sc.nextLine().trim();
        List<Product> result = productService.searchByKeyword(keyword);
        printProductTable(result, "KẾT QUẢ TÌM KIẾM");
    }

    private void searchByPriceRange() {
        System.out.print("Giá từ: ");
        BigDecimal min = getValidBigDecimal();
        System.out.print("đến: ");
        BigDecimal max = getValidBigDecimal();

        List<Product> result = productService.getByPriceRange(min, max);
        printProductTable(result, "SẢN PHẨM TRONG KHOẢNG GIÁ");
    }

    private void searchProductsInStock() {
        System.out.print("Nhập từ khóa tìm sản phẩm còn hàng: ");
        String keyword = sc.nextLine().trim();
        List<Product> result = productService.searchByNameAndInStock(keyword);
        printProductTable(result, "SẢN PHẨM CÒN HÀNG");
    }

    private void addNewProduct() {
        Product p = new Product();
        System.out.print("Tên sản phẩm: ");
        p.setName(sc.nextLine().trim());

        System.out.print("Thương hiệu: ");
        p.setBrand(sc.nextLine().trim());

        System.out.print("Giá bán: ");
        p.setPrice(getValidBigDecimal());

        System.out.print("Số lượng tồn kho: ");
        p.setStock(getValidInt());

        if (productService.addNewProduct(p)) {
            System.out.println("→ Thêm sản phẩm thành công!");
        } else {
            System.out.println("→ Thêm thất bại!");
        }
    }

    private void updateProduct() {
        System.out.print("Nhập ID sản phẩm cần sửa: ");
        int id = getValidInt();
        Product existing = productService.findById(id);

        if (existing == null) {
            System.out.println("Không tìm thấy sản phẩm!");
            return;
        }

        System.out.println("Thông tin hiện tại:");
        printSingleProduct(existing);

        Product updated = new Product();
        updated.setId(id);

        System.out.print("Tên mới (Enter giữ nguyên): ");
        String name = sc.nextLine().trim();
        updated.setName(name.isEmpty() ? existing.getName() : name);

        System.out.print("Thương hiệu mới (Enter giữ nguyên): ");
        String brand = sc.nextLine().trim();
        updated.setBrand(brand.isEmpty() ? existing.getBrand() : brand);

        System.out.print("Giá mới (Enter giữ nguyên): ");
        String priceStr = sc.nextLine().trim();
        updated.setPrice(priceStr.isEmpty() ? existing.getPrice() : new BigDecimal(priceStr));

        System.out.print("Tồn kho mới (Enter giữ nguyên): ");
        String stockStr = sc.nextLine().trim();
        updated.setStock(stockStr.isEmpty() ? existing.getStock() : Integer.parseInt(stockStr));

        if (productService.updateProduct(updated)) {
            System.out.println("→ Cập nhật thành công!");
        } else {
            System.out.println("→ Cập nhật thất bại!");
        }
    }

    private void deleteProduct() {
        System.out.print("Nhập ID sản phẩm muốn xóa: ");
        int id = getValidInt();

        Product p = productService.findById(id);
        if (p == null) {
            System.out.println("Không tìm thấy sản phẩm!");
            return;
        }

        System.out.println("Bạn có chắc chắn muốn xóa?");
        printSingleProduct(p);
        System.out.print("Nhập Y để xác nhận, N để hủy: ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if ("Y".equals(confirm)) {
            if (productService.deleteProduct(id)) {
                System.out.println("→ Xóa thành công!");
            } else {
                System.out.println("→ Xóa thất bại! (có thể sản phẩm đang được sử dụng trong hóa đơn)");
            }
        } else {
            System.out.println("Đã hủy xóa.");
        }
    }

    private void printProductTable(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào!");
            return;
        }

        System.out.println("\n" + title);
        System.out.println("┌─────┬───────────────────────────────────────┬──────────────┬───────────────┬────────┐");
        System.out.println("│ ID  │ Tên sản phẩm                          │ Thương hiệu  │ Giá bán       │ Tồn kho│");
        System.out.println("├─────┼───────────────────────────────────────┼──────────────┼───────────────┼────────┤");

        for (Product p : products) {
            System.out.printf("│ %-3d │ %-37s │ %-12s │ %,13.0f │ %6d │\n",
                    p.getId(), truncate(p.getName(), 37),
                    p.getBrand(),
                    p.getPrice(),
                    p.getStock());
        }

        System.out.println("└─────┴───────────────────────────────────────┴──────────────┴───────────────┴────────┘");
    }

    private void printSingleProduct(Product p) {
        System.out.println("ID: " + p.getId());
        System.out.println("Tên: " + p.getName());
        System.out.println("Thương hiệu: " + p.getBrand());
        System.out.println("Giá: " + String.format("%,.0f", p.getPrice()) + " VNĐ");
        System.out.println("Tồn kho: " + p.getStock());
    }

    private String truncate(String str, int maxLength) {
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

    private BigDecimal getValidBigDecimal() {
        while (true) {
            try {
                return new BigDecimal(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Vui lòng nhập số hợp lệ: ");
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