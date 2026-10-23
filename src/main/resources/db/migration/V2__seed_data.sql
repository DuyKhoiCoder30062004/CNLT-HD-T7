INSERT INTO roles (name, description) VALUES
('ADMIN', 'Quản trị hệ thống'),
('USER', 'Khách hàng');

INSERT INTO categories (name, description) VALUES
('WATCH', 'Các loại đồng hồ'),
('PHONE', 'Các loại điện thoại'),
('LAPTOP', 'Máy tính xách tay');

INSERT INTO statuses (name, description) VALUES
('PENDING', 'Đơn hàng đang chờ xác nhận'),
('CONFIRMED', 'Đơn hàng đã được xác nhận'),
('SHIPPING', 'Đơn hàng đang được giao'),
('COMPLETED', 'Đơn hàng đã hoàn thành'),
('CANCELLED', 'Đơn hàng đã bị hủy');

INSERT INTO provinces (name) VALUES
('Thành phố Hồ Chí Minh'),
('Thành phố Hà Nội');

INSERT INTO wards (name, province_id) VALUES
(
    'Phường Bến Nghé',
    (SELECT id FROM provinces
     WHERE name = 'Thành phố Hồ Chí Minh')
),
(
    'Phường Bến Thành',
    (SELECT id FROM provinces
     WHERE name = 'Thành phố Hồ Chí Minh')
),
(
    'Phường Cửa Nam',
    (SELECT id FROM provinces
     WHERE name = 'Thành phố Hà Nội')
);

INSERT INTO receivers (fullname, phone, ward_id, created_at) VALUES
(
    'Nguyễn Văn An',
    '0901000001',
    (SELECT id FROM wards WHERE name = 'Phường Bến Nghé'),
    CURRENT_TIMESTAMP
),
(
    'Trần Thị Bình',
    '0901000002',
    (SELECT id FROM wards WHERE name = 'Phường Bến Thành'),
    CURRENT_TIMESTAMP
),
(
    'Lê Văn Cường',
    '0901000003',
    (SELECT id FROM wards WHERE name = 'Phường Cửa Nam'),
    CURRENT_TIMESTAMP
);

INSERT INTO products (
    name, price, thumbnail, weight, description,
    discount, total_price, category_id,
    inventory_quantity, sold_quantity, created_at, is_deleted
) VALUES
(
    'iPhone 16', 22000000, '/imgs/product/iphone16.jpg',
    170, 'Điện thoại Apple iPhone 16',
    5, 20900000,
    (SELECT id FROM categories WHERE name = 'PHONE'),
    50, 20, CURRENT_TIMESTAMP, FALSE
),
(
    'Samsung Galaxy S25', 20000000,
    '/imgs/product/samsung-s25.jpg',
    162, 'Điện thoại Samsung Galaxy S25',
    10, 18000000,
    (SELECT id FROM categories WHERE name = 'PHONE'),
    40, 15, CURRENT_TIMESTAMP, FALSE
),
(
    'Xiaomi Redmi Note 14', 6000000,
    '/imgs/product/redmi-note14.jpg',
    190, 'Điện thoại Xiaomi phân khúc tầm trung',
    0, 6000000,
    (SELECT id FROM categories WHERE name = 'PHONE'),
    100, 35, CURRENT_TIMESTAMP, FALSE
),
(
    'MacBook Air M4', 27000000,
    '/imgs/product/macbook-air-m4.jpg',
    1240, 'Laptop Apple MacBook Air sử dụng chip M4',
    5, 25650000,
    (SELECT id FROM categories WHERE name = 'LAPTOP'),
    25, 10, CURRENT_TIMESTAMP, FALSE
),
(
    'ASUS Vivobook 15', 15000000,
    '/imgs/product/asus-vivobook15.jpg',
    1700, 'Laptop ASUS phục vụ học tập và làm việc',
    10, 13500000,
    (SELECT id FROM categories WHERE name = 'LAPTOP'),
    30, 12, CURRENT_TIMESTAMP, FALSE
);

INSERT INTO images (product_id, path, alt) VALUES
(
    (SELECT id FROM products WHERE name = 'iPhone 16'),
    '/imgs/product/iphone16.jpg', 'iPhone 16 mặt trước'
),
(
    (SELECT id FROM products WHERE name = 'iPhone 16'),
    '/imgs/product/iphone16-back.jpg', 'iPhone 16 mặt sau'
),
(
    (SELECT id FROM products WHERE name = 'Samsung Galaxy S25'),
    '/imgs/product/samsung-s25.jpg', 'Samsung Galaxy S25'
),
(
    (SELECT id FROM products WHERE name = 'Xiaomi Redmi Note 14'),
    '/imgs/product/redmi-note14.jpg', 'Xiaomi Redmi Note 14'
),
(
    (SELECT id FROM products WHERE name = 'MacBook Air M4'),
    '/imgs/product/macbook-air-m4.jpg', 'MacBook Air M4'
),
(
    (SELECT id FROM products WHERE name = 'MacBook Air M4'),
    '/imgs/product/macbook-air-m4-side.jpg', 'MacBook Air M4 nhìn nghiêng'
),
(
    (SELECT id FROM products WHERE name = 'ASUS Vivobook 15'),
    '/imgs/product/asus-vivobook15.jpg', 'ASUS Vivobook 15'
);

-- Password123
INSERT INTO users (
    username, phone, email, password, avatar, role_id, created_at
) VALUES
(
    'admin', '0901000101', 'admin@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/admin.jpg',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    CURRENT_TIMESTAMP
),
(
    'nguyenvanan', '0901000102', 'an@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/an.jpg',
    (SELECT id FROM roles WHERE name = 'USER'),
    CURRENT_TIMESTAMP
),
(
    'tranthibinh', '0901000103', 'binh@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/binh.jpg',
    (SELECT id FROM roles WHERE name = 'USER'),
    CURRENT_TIMESTAMP
),
(
    'levancuong', '0901000104', 'cuong@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/cuong.jpg',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    CURRENT_TIMESTAMP
);

INSERT INTO payment_statuses (name, description) VALUES
('UNPAID', 'Chưa thanh toán'),
('PAID', 'Đã thanh toán'),
('FAILED', 'Thanh toán thất bại'),
('REFUNDED', 'Đã hoàn tiền');

INSERT INTO payments (
    payment_status_id, method, amount, transaction_id, created_at
) VALUES
(
    (SELECT id FROM payment_statuses WHERE name = 'PAID'),
    'BANK_TRANSFER', 46550000, 'TXN_DEMO_001', CURRENT_TIMESTAMP
),
(
    (SELECT id FROM payment_statuses WHERE name = 'PAID'),
    'CASH', 36000000, 'TXN_DEMO_002', CURRENT_TIMESTAMP
),
(
    (SELECT id FROM payment_statuses WHERE name = 'UNPAID'),
    'COD', 25500000, NULL, CURRENT_TIMESTAMP
);

INSERT INTO orders (
    receiver_id, user_id, price, total_price,
    status_id, payment_id, created_at
) VALUES
(
    (SELECT id FROM receivers WHERE fullname = 'Nguyễn Văn An'),
    (SELECT id FROM users WHERE email = 'an@example.com'),
    46550000, 46550000,
    (SELECT id FROM statuses WHERE name = 'COMPLETED'),
    (SELECT id FROM payments WHERE transaction_id = 'TXN_DEMO_001'),
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM receivers WHERE fullname = 'Trần Thị Bình'),
    (SELECT id FROM users WHERE email = 'binh@example.com'),
    36000000, 36000000,
    (SELECT id FROM statuses WHERE name = 'SHIPPING'),
    (SELECT id FROM payments WHERE transaction_id = 'TXN_DEMO_002'),
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM receivers WHERE fullname = 'Lê Văn Cường'),
    (SELECT id FROM users WHERE email = 'an@example.com'),
    25500000, 25500000,
    (SELECT id FROM statuses WHERE name = 'PENDING'),
    (SELECT id FROM payments
     WHERE payment_status_id = (
         SELECT id FROM payment_statuses WHERE name = 'UNPAID'
     )
     AND method = 'COD'
     AND transaction_id IS NULL),
    CURRENT_TIMESTAMP
);

INSERT INTO order_details (
    product_id, order_id, price, quantity, total_price
) VALUES
(
    (SELECT id FROM products WHERE name = 'iPhone 16'),
    (
        SELECT o.id
        FROM orders o
        JOIN receivers r ON r.id = o.receiver_id
        JOIN users u ON u.id = o.user_id
        WHERE r.fullname = 'Nguyễn Văn An'
          AND u.email = 'an@example.com'
    ),
    20900000, 1, 20900000
),
(
    (SELECT id FROM products WHERE name = 'MacBook Air M4'),
    (
        SELECT o.id
        FROM orders o
        JOIN receivers r ON r.id = o.receiver_id
        JOIN users u ON u.id = o.user_id
        WHERE r.fullname = 'Nguyễn Văn An'
          AND u.email = 'an@example.com'
    ),
    25650000, 1, 25650000
),
(
    (SELECT id FROM products WHERE name = 'Samsung Galaxy S25'),
    (
        SELECT o.id
        FROM orders o
        JOIN receivers r ON r.id = o.receiver_id
        JOIN users u ON u.id = o.user_id
        WHERE r.fullname = 'Trần Thị Bình'
          AND u.email = 'binh@example.com'
    ),
    18000000, 2, 36000000
),
(
    (SELECT id FROM products WHERE name = 'ASUS Vivobook 15'),
    (
        SELECT o.id
        FROM orders o
        JOIN receivers r ON r.id = o.receiver_id
        JOIN users u ON u.id = o.user_id
        WHERE r.fullname = 'Lê Văn Cường'
          AND u.email = 'an@example.com'
    ),
    13500000, 1, 13500000
),
(
    (SELECT id FROM products WHERE name = 'Xiaomi Redmi Note 14'),
    (
        SELECT o.id
        FROM orders o
        JOIN receivers r ON r.id = o.receiver_id
        JOIN users u ON u.id = o.user_id
        WHERE r.fullname = 'Lê Văn Cường'
          AND u.email = 'an@example.com'
    ),
    6000000, 2, 12000000
);