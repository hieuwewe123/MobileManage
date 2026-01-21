package ra.dao;

import ra.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface IProductDAO {
    // Lấy tất cả sản phẩm
    List<Product> getAllProducts();

    // Tìm kiếm theo từ khóa (tên hoặc brand)
    List<Product> searchByKeyword(String keyword);

    // Tìm theo khoảng giá
    List<Product> getByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // Thêm sản phẩm mới
    boolean addProduct(Product product);

    // Cập nhật sản phẩm
    boolean updateProduct(Product product);

    // Xóa sản phẩm
    boolean deleteProduct(int productId);

    // Lấy sản phẩm theo ID
    Product getProductById(int id);
    List<Product> searchByNameAndInStock(String keyword);
}
