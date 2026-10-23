INSERT INTO roles (id, name, description) VALUES
(1, 'ADMIN', 'Quản trị hệ thống'),
(2, 'USER', 'Khách hàng');

INSERT INTO categories (id, name, description) VALUES
(1, 'WATCH', 'Các loại đồng hồ'),
(2, 'PHONE', 'Các loại điện thoại'),
(3, 'LAPTOP', 'Máy tính xách tay');

INSERT INTO statuses (id, name, description) VALUES
(1, 'PENDING', 'Đơn hàng đang chờ xác nhận'),
(2, 'CONFIRMED', 'Đơn hàng đã được xác nhận'),
(3, 'SHIPPING', 'Đơn hàng đang được giao'),
(4, 'COMPLETED', 'Đơn hàng đã hoàn thành'),
(5, 'CANCELLED', 'Đơn hàng đã bị hủy');

INSERT INTO provinces (id, name) VALUES
(1, 'Thành phố Hồ Chí Minh'),
(2, 'Thành phố Hà Nội');

INSERT INTO wards (id, name, province_id) VALUES
(1, 'Phường Bến Nghé', 1),
(2, 'Phường Bến Thành', 1),
(3, 'Phường Cửa Nam', 2);

INSERT INTO receivers (
    id, fullname, phone, ward_id, created_at
) VALUES
(1, 'Nguyễn Văn An', '0901000001', 1, CURRENT_TIMESTAMP),
(2, 'Trần Thị Bình', '0901000002', 2, CURRENT_TIMESTAMP),
(3, 'Lê Văn Cường', '0901000003', 3, CURRENT_TIMESTAMP);

INSERT INTO products (
    id, name, price, thumbnail, weight, description,
    discount, total_price, category_id,
    inventory_quantity, sold_quantity, created_at, is_deleted
) VALUES
(
    1, 'iPhone 16', 22000000, '/imgs/product/iphone16.jpg',
    170, 'Điện thoại Apple iPhone 16',
    5, 20900000, 2, 50, 20, CURRENT_TIMESTAMP, FALSE
),
(
    2, 'Samsung Galaxy S25', 20000000, '/imgs/product/samsung-s25.jpg',
    162, 'Điện thoại Samsung Galaxy S25',
    10, 18000000, 2, 40, 15, CURRENT_TIMESTAMP, FALSE
),
(
    3, 'Xiaomi Redmi Note 14', 6000000, '/imgs/product/redmi-note14.jpg',
    190, 'Điện thoại Xiaomi phân khúc tầm trung',
    0, 6000000, 2, 100, 35, CURRENT_TIMESTAMP, FALSE
),
(
    4, 'MacBook Air M4', 27000000, '/imgs/product/macbook-air-m4.jpg',
    1240, 'Laptop Apple MacBook Air sử dụng chip M4',
    5, 25650000, 3, 25, 10, CURRENT_TIMESTAMP, FALSE
),
(
    5, 'ASUS Vivobook 15', 15000000, '/imgs/product/asus-vivobook15.jpg',
    1700, 'Laptop ASUS phục vụ học tập và làm việc',
    10, 13500000, 3, 30, 12, CURRENT_TIMESTAMP, FALSE
);

INSERT INTO images (id, product_id, path, alt) VALUES
(1, 1, '/imgs/product/iphone16.jpg', 'iPhone 16 mặt trước'),
(2, 1, '/imgs/product/iphone16-back.jpg', 'iPhone 16 mặt sau'),
(3, 2, '/imgs/product/samsung-s25.jpg', 'Samsung Galaxy S25'),
(4, 3, '/imgs/product/redmi-note14.jpg', 'Xiaomi Redmi Note 14'),
(5, 4, '/imgs/product/macbook-air-m4.jpg', 'MacBook Air M4'),
(6, 4, '/imgs/product/macbook-air-m4-side.jpg', 'MacBook Air M4 nhìn nghiêng'),
(7, 5, '/imgs/product/asus-vivobook15.jpg', 'ASUS Vivobook 15');

INSERT INTO users (
    id, username, phone, email, password, avatar, role_id, created_at
) VALUES
(
    1, 'admin', '0901000101', 'admin@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/admin.jpg', 1, CURRENT_TIMESTAMP
),
(
    2, 'nguyenvanan', '0901000102', 'an@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/an.jpg', 2, CURRENT_TIMESTAMP
),
(
    3, 'tranthibinh', '0901000103', 'binh@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/binh.jpg', 2, CURRENT_TIMESTAMP
),
(
    4, 'levancuong', '0901000104', 'cuong@example.com',
    '$2a$10$kCLfZBaLWwsLiUpFd2tqm.bB9Mh0fDd7qJyhNSnceFnXqdNPLL/Fq',
    '/imgs/avatars/cuong.jpg', 1, CURRENT_TIMESTAMP
);

INSERT INTO payment_statuses (id, name, description) VALUES
(1, 'UNPAID', 'Chưa thanh toán'),
(2, 'PAID', 'Đã thanh toán'),
(3, 'FAILED', 'Thanh toán thất bại'),
(4, 'REFUNDED', 'Đã hoàn tiền');

INSERT INTO payments (
    id, payment_status_id, method, amount, transaction_id, created_at
) VALUES
(1, 2, 'BANK_TRANSFER', 46550000, 'TXN_DEMO_001', CURRENT_TIMESTAMP),
(2, 2, 'CASH', 36000000, 'TXN_DEMO_002', CURRENT_TIMESTAMP),
(3, 1, 'COD', 25500000, NULL, CURRENT_TIMESTAMP);

INSERT INTO orders (
    id, receiver_id, user_id, price, total_price,
    status_id, payment_id, created_at
) VALUES
(1, 1, 2, 46550000, 46550000, 4, 1, CURRENT_TIMESTAMP),
(2, 2, 3, 36000000, 36000000, 3, 2, CURRENT_TIMESTAMP),
(3, 3, 2, 25500000, 25500000, 1, 3, CURRENT_TIMESTAMP);

INSERT INTO order_details (
    id, product_id, order_id, price, quantity, total_price
) VALUES
(1, 1, 1, 20900000, 1, 20900000),
(2, 4, 1, 25650000, 1, 25650000),
(3, 2, 2, 18000000, 2, 36000000),
(4, 5, 3, 13500000, 1, 13500000),
(5, 3, 3, 6000000, 2, 12000000);