CREATE DATABASE mobile_management;

CREATE TABLE admin
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE product
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100)   NOT NULL,
    brand VARCHAR(50)    NOT NULL,
    price DECIMAL(12, 2) NOT NULL CHECK (price >= 0),
    stock INT            NOT NULL CHECK (stock >= 0)
);

CREATE TABLE customer
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(20),
    email   VARCHAR(100) UNIQUE,
    address VARCHAR(255)
);

CREATE TABLE invoice
(
    id           SERIAL PRIMARY KEY,
    customer_id  INT            NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12, 2) NOT NULL CHECK (total_amount >= 0),

    CONSTRAINT fk_invoice_customer
        FOREIGN KEY (customer_id)
            REFERENCES customer (id)
            ON DELETE RESTRICT
);

CREATE TABLE invoice_detail
(
    id         SERIAL PRIMARY KEY,
    invoice_id INT            NOT NULL,
    product_id INT            NOT NULL,
    quantity   INT            NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12, 2) NOT NULL CHECK (unit_price >= 0),

    CONSTRAINT fk_detail_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES invoice (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_detail_product
        FOREIGN KEY (product_id)
            REFERENCES product (id)
            ON DELETE RESTRICT
);

INSERT INTO admin (username, password)
VALUES ('admin', '123456');

INSERT INTO product (name, brand, price, stock) VALUES
                                                    ('iPhone 11', 'Apple', 10990000, 5),
                                                    ('iPhone 12', 'Apple', 13990000, 8),
                                                    ('iPhone 13', 'Apple', 16990000, 10),
                                                    ('iPhone 14', 'Apple', 19990000, 12),
                                                    ('iPhone 15', 'Apple', 23990000, 6),
                                                    ('Galaxy S21', 'Samsung', 12990000, 7),
                                                    ('Galaxy S22', 'Samsung', 15990000, 9),
                                                    ('Galaxy S23', 'Samsung', 18990000, 11),
                                                    ('Galaxy A34', 'Samsung', 6990000, 20),
                                                    ('Galaxy A54', 'Samsung', 8990000, 18),
                                                    ('Redmi Note 11', 'Xiaomi', 3990000, 25),
                                                    ('Redmi Note 12', 'Xiaomi', 4990000, 30),
                                                    ('Redmi Note 13', 'Xiaomi', 5990000, 22),
                                                    ('Xiaomi 12T', 'Xiaomi', 10990000, 14),
                                                    ('Xiaomi 13', 'Xiaomi', 14990000, 10),
                                                    ('OPPO A57', 'OPPO', 4290000, 17),
                                                    ('OPPO A78', 'OPPO', 6990000, 15),
                                                    ('OPPO Reno 8', 'OPPO', 10990000, 9),
                                                    ('OPPO Reno 10', 'OPPO', 12990000, 8),
                                                    ('Vivo Y20', 'Vivo', 3790000, 20),
                                                    ('Vivo Y22', 'Vivo', 4590000, 18),
                                                    ('Vivo V25', 'Vivo', 8990000, 11),
                                                    ('Realme C55', 'Realme', 4990000, 19),
                                                    ('Realme 10', 'Realme', 6490000, 16),
                                                    ('Realme GT Neo 3', 'Realme', 11990000, 7);

INSERT INTO customer (name, phone, email, address) VALUES
                                                       ('Nguyen Van An', '0901111111', 'an@gmail.com', 'Hà Nội'),
                                                       ('Tran Thi Bich', '0902222222', 'bich@gmail.com', 'TP HCM'),
                                                       ('Le Van Cuong', '0903333333', 'cuong@gmail.com', 'Đà Nẵng'),
                                                       ('Pham Thi Dao', '0904444444', 'dao@gmail.com', 'Cần Thơ'),
                                                       ('Hoang Van Duc', '0905555555', 'duc@gmail.com', 'Hải Phòng'),
                                                       ('Do Thi Em', '0906666666', 'em@gmail.com', 'Bắc Ninh'),
                                                       ('Vu Van Giang', '0907777777', NULL, 'Quảng Ninh'),
                                                       ('Trieu Hoang Hieu', '0987654321', 'hieu@gmail.com', 'Hà Nội');

INSERT INTO invoice (customer_id, total_amount) VALUES
                                                    (1, 33980000),
                                                    (2, 8990000),
                                                    (3, 15990000),
                                                    (4, 22980000),
                                                    (5, 4990000);

INSERT INTO invoice_detail (invoice_id, product_id, quantity, unit_price) VALUES
                                                                              (1, 3, 1, 16990000),
                                                                              (1, 8, 1, 16990000),
                                                                              (2, 10, 1, 8990000),
                                                                              (3, 7, 1, 15990000),
                                                                              (4, 5, 1, 23990000),
                                                                              (5, 12, 1, 4990000);

CREATE OR REPLACE FUNCTION get_all_products()
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      brand VARCHAR(50),
                      price DECIMAL(12,2),
                      stock INTEGER
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.name, p.brand, p.price, p.stock
        FROM product p
        ORDER BY p.brand, p.name;
END;
$$;

CREATE OR REPLACE FUNCTION search_products_by_keyword(p_keyword VARCHAR)
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      brand VARCHAR(50),
                      price DECIMAL(12,2),
                      stock INTEGER
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.name, p.brand, p.price, p.stock
        FROM product p
        WHERE p.name ILIKE '%' || COALESCE(p_keyword, '') || '%'
           OR p.brand ILIKE '%' || COALESCE(p_keyword, '') || '%'
        ORDER BY p.brand, p.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_products_by_price_range(p_min DECIMAL(12,2), p_max DECIMAL(12,2))
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      brand VARCHAR(50),
                      price DECIMAL(12,2),
                      stock INTEGER
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.name, p.brand, p.price, p.stock
        FROM product p
        WHERE p.price BETWEEN p_min AND p_max
        ORDER BY p.price;
END;
$$;

CREATE OR REPLACE PROCEDURE add_new_product(
    p_name VARCHAR(100),
    p_brand VARCHAR(50),
    p_price DECIMAL(12,2),
    p_stock INTEGER
) LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO product (name, brand, price, stock)
    VALUES (TRIM(p_name), TRIM(p_brand), p_price, p_stock);
END;
$$;

CREATE OR REPLACE PROCEDURE update_product(
    p_id INTEGER,
    p_name VARCHAR(100),
    p_brand VARCHAR(50),
    p_price DECIMAL(12,2),
    p_stock INTEGER
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE product
    SET name = TRIM(p_name),
        brand = TRIM(p_brand),
        price = p_price,
        stock = p_stock
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy sản phẩm với ID %', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_product(p_id INTEGER)
    LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM product WHERE id = p_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy sản phẩm với ID %', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION search_products_by_name_and_in_stock(p_keyword VARCHAR)
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      brand VARCHAR(50),
                      price DECIMAL(12,2),
                      stock INTEGER
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.name, p.brand, p.price, p.stock
        FROM product p
        WHERE (p.name ILIKE '%' || COALESCE(p_keyword, '') || '%'
            OR p.brand ILIKE '%' || COALESCE(p_keyword, '') || '%')
          AND p.stock > 0
        ORDER BY p.brand, p.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_customers()
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      phone VARCHAR(20),
                      email VARCHAR(100),
                      address VARCHAR(255)
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT c.id, c.name, c.phone, c.email, c.address
        FROM customer c
        ORDER BY c.name;
END;
$$;

CREATE OR REPLACE FUNCTION search_customers(p_keyword VARCHAR)
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      phone VARCHAR(20),
                      email VARCHAR(100),
                      address VARCHAR(255)
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT c.id, c.name, c.phone, c.email, c.address
        FROM customer c
        WHERE c.name ILIKE '%' || COALESCE(p_keyword, '') || '%'
           OR c.phone ILIKE '%' || COALESCE(p_keyword, '') || '%'
           OR c.email ILIKE '%' || COALESCE(p_keyword, '') || '%'
        ORDER BY c.name;
END;
$$;

CREATE OR REPLACE PROCEDURE add_new_customer(
    p_name    VARCHAR(100),
    p_phone   VARCHAR(20),
    p_email   VARCHAR(100),
    p_address VARCHAR(255)
) LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO customer (name, phone, email, address)
    VALUES (TRIM(p_name), TRIM(p_phone), TRIM(p_email), TRIM(p_address));
END;
$$;

CREATE OR REPLACE PROCEDURE update_customer(
    p_id      INTEGER,
    p_name    VARCHAR(100),
    p_phone   VARCHAR(20),
    p_email   VARCHAR(100),
    p_address VARCHAR(255)
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE customer
    SET name    = TRIM(p_name),
        phone   = TRIM(p_phone),
        email   = TRIM(p_email),
        address = TRIM(p_address)
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy khách hàng với ID %', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_customer(p_id INTEGER)
    LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM customer WHERE id = p_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy khách hàng với ID %', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION get_customer_by_id(p_id INTEGER)
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      phone VARCHAR(20),
                      email VARCHAR(100),
                      address VARCHAR(255)
                  ) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT c.id, c.name, c.phone, c.email, c.address
        FROM customer c
        WHERE c.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION check_admin_login(
    p_username VARCHAR(50),
    p_password VARCHAR(255)
) RETURNS BOOLEAN LANGUAGE plpgsql AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM admin
        WHERE username = TRIM(p_username)
          AND password = TRIM(p_password)
    );
END;
$$;

-- ========================================
-- INVOICE MANAGEMENT FUNCTIONS & PROCEDURES
-- ========================================

-- 1. Function: Tạo hóa đơn mới (trả về ID hoặc -1 nếu lỗi)
CREATE OR REPLACE FUNCTION add_invoice(p_customer_id INT)
RETURNS INT AS $$
DECLARE
    new_invoice_id INT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM customer WHERE id = p_customer_id) THEN
        RETURN -1;
    END IF;
    
    INSERT INTO invoice (customer_id, created_at, total_amount)
    VALUES (p_customer_id, NOW(), 0)
    RETURNING id INTO new_invoice_id;
    
    RETURN new_invoice_id;
END;
$$ LANGUAGE plpgsql;

-- 2. Procedure: Thêm chi tiết hóa đơn (tự động cập nhật stock + total)
CREATE OR REPLACE PROCEDURE add_invoice_detail(
    IN p_invoice_id INT,
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_unit_price NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    BEGIN
        -- Thêm chi tiết hóa đơn
        INSERT INTO invoice_detail (invoice_id, product_id, quantity, unit_price)
        VALUES (p_invoice_id, p_product_id, p_quantity, p_unit_price);
        
        -- Cập nhật tồn kho sản phẩm
        UPDATE product 
        SET stock = stock - p_quantity
        WHERE id = p_product_id;
        
        -- Cập nhật tổng tiền hóa đơn (tính lại từ tất cả invoice_detail)
        UPDATE invoice
        SET total_amount = COALESCE((
            SELECT SUM(quantity * unit_price)
            FROM invoice_detail
            WHERE invoice_id = p_invoice_id
        ), 0)
        WHERE id = p_invoice_id;
        
    EXCEPTION WHEN OTHERS THEN
        RAISE EXCEPTION 'Lỗi khi thêm chi tiết hóa đơn: %', SQLERRM;
    END;
END;
$$;

-- 3. Function: Lấy tất cả hóa đơn
CREATE OR REPLACE FUNCTION get_all_invoices()
RETURNS TABLE(id INT, customer_id INT, created_at TIMESTAMP, total_amount NUMERIC, customer_name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        i.id,
        i.customer_id,
        i.created_at,
        i.total_amount,
        c.name as customer_name
    FROM invoice i
    LEFT JOIN customer c ON i.customer_id = c.id
    ORDER BY i.id DESC;
END;
$$ LANGUAGE plpgsql;

-- 4. Function: Tìm kiếm theo tên khách hàng
CREATE OR REPLACE FUNCTION search_invoices_by_customer(p_keyword TEXT)
RETURNS TABLE(id INT, customer_id INT, created_at TIMESTAMP, total_amount NUMERIC, customer_name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        i.id,
        i.customer_id,
        i.created_at,
        i.total_amount,
        c.name as customer_name
    FROM invoice i
    LEFT JOIN customer c ON i.customer_id = c.id
    WHERE LOWER(c.name) LIKE LOWER('%' || p_keyword || '%')
    ORDER BY i.id DESC;
END;
$$ LANGUAGE plpgsql;

-- 5. Function: Tìm kiếm theo ngày/tháng/năm (hỗ trợ format: YYYY-MM-DD, YYYY-MM, YYYY)
CREATE OR REPLACE FUNCTION search_invoices_by_date(p_date_str TEXT)
RETURNS TABLE(id INT, customer_id INT, created_at TIMESTAMP, total_amount NUMERIC, customer_name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        i.id,
        i.customer_id,
        i.created_at,
        i.total_amount,
        c.name as customer_name
    FROM invoice i
    LEFT JOIN customer c ON i.customer_id = c.id
    WHERE TO_CHAR(i.created_at, 'YYYY-MM-DD') LIKE p_date_str || '%'
       OR TO_CHAR(i.created_at, 'YYYY-MM') = p_date_str
       OR TO_CHAR(i.created_at, 'YYYY') = p_date_str
    ORDER BY i.id DESC;
END;
$$ LANGUAGE plpgsql;

-- 6. Function: Doanh thu theo ngày
CREATE OR REPLACE FUNCTION get_revenue_by_day()
RETURNS TABLE(ngay DATE, tong_doanh_thu NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        DATE(i.created_at) as ngay,
        SUM(i.total_amount) as tong_doanh_thu
    FROM invoice i
    GROUP BY DATE(i.created_at)
    ORDER BY ngay DESC;
END;
$$ LANGUAGE plpgsql;

-- 7. Function: Doanh thu theo tháng
CREATE OR REPLACE FUNCTION get_revenue_by_month()
RETURNS TABLE(thang VARCHAR, tong_doanh_thu NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        TO_CHAR(i.created_at, 'YYYY-MM') as thang,
        SUM(i.total_amount) as tong_doanh_thu
    FROM invoice i
    GROUP BY TO_CHAR(i.created_at, 'YYYY-MM')
    ORDER BY thang DESC;
END;
$$ LANGUAGE plpgsql;

-- 8. Function: Doanh thu theo năm
CREATE OR REPLACE FUNCTION get_revenue_by_year()
RETURNS TABLE(nam VARCHAR, tong_doanh_thu NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        TO_CHAR(i.created_at, 'YYYY') as nam,
        SUM(i.total_amount) as tong_doanh_thu
    FROM invoice i
    GROUP BY TO_CHAR(i.created_at, 'YYYY')
    ORDER BY nam DESC;
END;
$$ LANGUAGE plpgsql;

-- 9. Function: Trigger cập nhật tổng tiền khi xóa chi tiết
CREATE OR REPLACE FUNCTION after_delete_invoice_detail()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE invoice
    SET total_amount = COALESCE((
        SELECT SUM(quantity * unit_price)
        FROM invoice_detail
        WHERE invoice_id = OLD.invoice_id
    ), 0)
    WHERE id = OLD.invoice_id;
    
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- 10. Trigger: Tự động cập nhật tổng tiền khi xóa
CREATE TRIGGER after_delete_invoice_detail
AFTER DELETE ON invoice_detail
FOR EACH ROW
EXECUTE FUNCTION after_delete_invoice_detail();