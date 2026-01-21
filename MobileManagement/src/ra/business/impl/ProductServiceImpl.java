package ra.business.impl;

import ra.dao.IProductDAO;
import ra.dao.impl.ProductDAOImpl;
import ra.model.Product;
import ra.business.IProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements IProductService {

    private final IProductDAO productDAO = new ProductDAOImpl();

    public ProductServiceImpl(ProductDAOImpl productDAO) {
    }

    @Override
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.searchByKeyword(keyword.trim());
    }

    @Override
    public List<Product> getByPriceRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null || min.compareTo(max) > 0) {
            return new ArrayList<>();
        }
        return productDAO.getByPriceRange(min, max);
    }

    @Override
    public boolean addNewProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (product.getStock() < 0) {
            return false;
        }
        return productDAO.addProduct(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        if (product.getId() <= 0) return false;
        return productDAO.updateProduct(product);
    }

    @Override
    public boolean deleteProduct(int id) {
        if (id <= 0) return false;
        return productDAO.deleteProduct(id);
    }

    @Override
    public Product findById(int id) {
        return productDAO.getProductById(id);
    }
    @Override
    public List<Product> searchByNameAndInStock(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Nếu không nhập từ khóa → trả tất cả còn hàng
            return productDAO.getAllProducts().stream()
                    .filter(p -> p.getStock() > 0)
                    .toList();
        }
        return productDAO.searchByNameAndInStock(keyword.trim());
    }
}