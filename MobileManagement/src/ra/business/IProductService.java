package ra.business;

import ra.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    List<Product> searchByKeyword(String keyword);
    List<Product> getByPriceRange(BigDecimal min, BigDecimal max);

    boolean addNewProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    List<Product> searchByNameAndInStock(String keyword);
    Product findById(int id);
}
