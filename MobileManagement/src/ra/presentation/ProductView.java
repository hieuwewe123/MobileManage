package ra.presentation;

import ra.business.IProductService;
import ra.business.impl.ProductServiceImpl;
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
            System.out.println("\n========== QUẢN LÝ SẢN PHẨM ==========");
            System.out.println("1. Hiển thị tất cả sản phẩm");
            System.out.println("2. Tìm kiếm sản phẩm (theo tên/brand)");
            System.out.println("3. Tìm kiếm theo khoảng giá");
            System.out.println("4. Thêm sản phẩm mới");
            System.out.println("5. Cập nhật sản phẩm");
            System.out.println("6. Xóa sản phẩm");
            System.out.println("0. Quay lại menu chính");
            System.out.print("Nhập lựa chọn: ");

            int choice = getValidInt();

            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    searchProducts();
                    break;
                case 3:
                    searchByPriceRange();
                    break;
                case 4:
                    addNewProduct();
                    break;
                case 5:
                    updateProduct();
                    break;
                case 6:
                    deleteProduct();
                    break;
                case 7:
                    searchProductsInStock();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
    private void searchProductsInStock() {
        System.out.print("Nhập từ khóa tìm sản phẩm còn hàng: ");
        String keyword = sc.nextLine().trim();
        List<Product> result = productService.searchByNameAndInStock(keyword);
        if (result.isEmpty()) {
            System.out.println("Không tìm thấy sản phẩm nào còn hàng phù hợp!");
        } else {
            System.out.println("Kết quả sản phẩm còn hàng:");
            printProductTable(result);
        }
    }
    private void displayAllProducts() {
        List<Product> products = productService.getAllProducts();
        printProductTable(products);
    }

    private void searchProducts() {
        System.out.print("Nhập từ khóa tìm kiếm: ");
        String keyword = sc.nextLine().trim();
        List<Product> result = productService.searchByKeyword(keyword);
        printProductTable(result);
    }

    private void searchByPriceRange() {
        System.out.print("Giá từ: ");
        BigDecimal min = getValidBigDecimal();
        System.out.print("đến: ");
        BigDecimal max = getValidBigDecimal();

        List<Product> result = productService.getByPriceRange(min, max);
        printProductTable(result);
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

    // Hiển thị bảng sản phẩm
    private void printProductTable(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào!");
            return;
        }

        System.out.println("│ ID  │ Tên sản phẩm                          │ Thương hiệu  │ Giá bán       │ Tồn kho│");

        for (Product p : products) {
            System.out.printf("│ %-3d │ %-37s │ %-12s │ %,13.0f │ %6d │\n",
                    p.getId(), truncate(p.getName(), 37),
                    p.getBrand(),
                    p.getPrice(),
                    p.getStock());
        }
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
}