create database mobile_management;

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
INSERT INTO product (name, brand, price, stock) VALUES
-- Apple
('iPhone 11', 'Apple', 10990000, 5),
('iPhone 12', 'Apple', 13990000, 8),
('iPhone 13', 'Apple', 16990000, 10),
('iPhone 14', 'Apple', 19990000, 12),
('iPhone 15', 'Apple', 23990000, 6),

-- Samsung
('Galaxy S21', 'Samsung', 12990000, 7),
('Galaxy S22', 'Samsung', 15990000, 9),
('Galaxy S23', 'Samsung', 18990000, 11),
('Galaxy A34', 'Samsung', 6990000, 20),
('Galaxy A54', 'Samsung', 8990000, 18),

-- Xiaomi
('Redmi Note 11', 'Xiaomi', 3990000, 25),
('Redmi Note 12', 'Xiaomi', 4990000, 30),
('Redmi Note 13', 'Xiaomi', 5990000, 22),
('Xiaomi 12T', 'Xiaomi', 10990000, 14),
('Xiaomi 13', 'Xiaomi', 14990000, 10),

-- OPPO
('OPPO A57', 'OPPO', 4290000, 17),
('OPPO A78', 'OPPO', 6990000, 15),
('OPPO Reno 8', 'OPPO', 10990000, 9),
('OPPO Reno 10', 'OPPO', 12990000, 8),

-- Vivo
('Vivo Y20', 'Vivo', 3790000, 20),
('Vivo Y22', 'Vivo', 4590000, 18),
('Vivo V25', 'Vivo', 8990000, 11),

-- Realme
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
                                                       ('Vu Van Giang', '0907777777', NULL, 'Quảng Ninh');

INSERT INTO invoice (customer_id, total_amount) VALUES
                                                    (1, 33980000),
                                                    (2, 8990000),
                                                    (3, 15990000),
                                                    (4, 22980000),
                                                    (5, 4990000);

INSERT INTO invoice_detail (invoice_id, product_id, quantity, unit_price) VALUES
-- Invoice 1
(1, 3, 1, 16990000),   -- iPhone 13
(1, 8, 1, 16990000),   -- Galaxy S22

-- Invoice 2
(2, 10, 1, 8990000),   -- Galaxy A54

-- Invoice 3
(3, 7, 1, 15990000),   -- Galaxy S22

-- Invoice 4
(4, 5, 1, 23990000),   -- iPhone 15

-- Invoice 5
(5, 12, 1, 4990000);   -- Redmi Note 12

-- 1. Lấy tất cả sản phẩm (bạn đã có, mình cải tiến một chút)
CREATE OR REPLACE FUNCTION get_all_products()
    RETURNS TABLE (
                      id INTEGER,
                      name VARCHAR(100),
                      brand VARCHAR(50),
                      price DECIMAL(12,2),
                      stock INTEGER
                  )
    LANGUAGE plpgsql
AS $$
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
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT p.id, p.name, p.brand, p.price, p.stock
        FROM product p
        WHERE p.name ILIKE '%' || p_keyword || '%'
           OR p.brand ILIKE '%' || p_keyword || '%'
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
                  )
    LANGUAGE plpgsql
AS $$
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
)
    LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO product (name, brand, price, stock)
    VALUES (p_name, p_brand, p_price, p_stock);

END;
$$;

CREATE OR REPLACE PROCEDURE update_product(
    p_id INTEGER,
    p_name VARCHAR(100),
    p_brand VARCHAR(50),
    p_price DECIMAL(12,2),
    p_stock INTEGER
)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE product
    SET name = p_name,
        brand = p_brand,
        price = p_price,
        stock = p_stock
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy sản phẩm với ID %', p_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_product(p_id INTEGER)
    LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM product WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy sản phẩm với ID %', p_id;
    END IF;
END;
$$;
