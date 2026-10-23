# 1. Tính năng: Authentication (Xác thực)

Tính năng này cung cấp xác thực người dùng và đăng ký mới.

* File cấu hình được lưu tại thư mục:
 `/docs/ccnlthd - auth service.postman_collection.json`


## Login (Đăng nhập)

Xác thực người dùng sử dụng số điện thoại và mật khẩu.

**Endpoint**

```http
POST /api/auth/login
```

**Request body**

```json
{
  "phone": "0987654321",
  "password": "password123"
}
```

**Response**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTg3NjU0MzIxIiwiaWF0IjoxNzkxMjAwOTg0LCJleHAiOjE3OTEyODczODR9.rDM6DUHl8tcS1-pHRnHmAMj2tMeDfXkt91KBvCo_f10",
    "user": {
        "username": "hahahahah",
        "email": "phuc@gmail.com",
        "phone": "0987654321",
        "password": "$2a$10$NnBD3O4JlW5hImZMDYj4EOFZVN3s1KsCXjdlZmxjWds40fk35heBi",
        "avatar": null,
        "createdAt": "2026-10-03T22:48:05.740201",
        "deletedAt": null,
        "id": 1,
        "role": "Khách hàng",
        "updatedAt": "2026-10-03T22:48:05.740201"
    },
    "type": "Bearer"
}
```
**Test endpoint bằng Postman**

![Màn hình Postman](./imgs/auth/login_postman.png)

---

## Register (Đăng ký)

Tạo người dùng mới.

**Endpoint**

```http
POST /api/auth/register
```

**Request body**

```json
{
  "phone": "0987654323",
  "username": "dr.donut",
  "password": "password123",
  "email": "donut@gmail.com"
}
```

**Response**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTg3NjU0MzIzIiwiaWF0IjoxNzkxMjAxNzE2LCJleHAiOjE3OTEyODgxMTZ9.oKMuxiSDoyo4b7SteigYPWdmpGMmuxb0VvLND3Q3X2A",
    "user": {
        "username": "dr.donut",
        "email": "donut@gmail.com",
        "phone": "0987654323",
        "password": "$2a$10$y7ai/rN2sNRcHiKnN5uxgeRftkTdiYDKRV8RiT71YiP9n.t1p752u",
        "avatar": null,
        "createdAt": "2026-10-05T19:01:55.759015",
        "deletedAt": null,
        "id": 3,
        "role": "Khách hàng",
        "updatedAt": "2026-10-05T19:01:55.7596641"
    },
    "type": "Bearer"
}
```

**Test endpoint bằng Postman**

![Màn hình Postman](./imgs/auth/register_postman.png)

---

## Lấy người dùng hiện tại

Lấy các thông tin người dùng hiện tại đang đăng nhập.

**Endpoint**

```http
GET /api/auth/me
```

**Headers**

```http
Authorization: Bearer <your-jwt-token>
```

Ví dụ:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTg3NjU0MzIzIiwiaWF0IjoxNzkxMjAxNzE2LCJleHAiOjE3OTEyODgxMTZ9.oKMuxiSDoyo4b7SteigYPWdmpGMmuxb0VvLND3Q3X2A
```

**Response**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTg3NjU0MzIzIiwiaWF0IjoxNzkxMjAxNzE2LCJleHAiOjE3OTEyODgxMTZ9.oKMuxiSDoyo4b7SteigYPWdmpGMmuxb0VvLND3Q3X2A",
    "user": {
        "username": "dr.donut",
        "email": "donut@gmail.com",
        "phone": "0987654323",
        "password": "$2a$10$y7ai/rN2sNRcHiKnN5uxgeRftkTdiYDKRV8RiT71YiP9n.t1p752u",
        "avatar": null,
        "createdAt": "2026-10-05T19:01:55.759015",
        "deletedAt": null,
        "id": 3,
        "role": "Khách hàng",
        "updatedAt": "2026-10-05T19:01:55.759664"
    },
    "type": "Bearer"
}
```

**Test endpoint bằng Postman**

![Màn hình Postman](./imgs/auth/me_postman.png)


## Dependencies yêu cầu

### JWT

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Ý nghĩa token

Token gồm 3 phần HEADER.PAYLOAD.SIGNATURE được tách ra bởi dấu chấm
Ví dụ:

```json
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwOTg3NjU0MzIzIiwiaWF0IjoxNzkxMjAxNzE2LCJleHAiOjE3OTEyODgxMTZ9.oKMuxiSDoyo4b7SteigYPWdmpGMmuxb0VvLND3Q3X2A",
```

**HEARDER**

```text
eyJhbGciOiJIUzI1NiJ9
```

Chuỗi trên cho server biết thuật toán nào đã được sử dụng. Trong trường hợp này dùng thuật toán HS256

```json
{
  "alg": "HS256"
}
```

**PAYLOAD**

```text
eyJzdWIiOiIwOTg3NjU0MzIzIiwiaWF0IjoxNzkxMjAxNzE2LCJleHAiOjE3OTEyODgxMTZ9
```

Chuỗi trên chứa các thông tin cần thiết trong trường hợp này là số điện thoại, thời điểm khởi tạo, thời điểm hết hạn (sau 24 giờ) như:

```json
{
  "sub": "0987654323",
  "iat": 1792388000,
  "exp": 1792474400
}
```

**SIGNATURE**

```text
oKMuxiSDoyo4b7SteigYPWdmpGMmuxb0VvLND3Q3X2A
```
Đây là phần khá quan trọng trong bảo mật để đảm bảo thông tin payload không bị thay đổi bởi người khác. Phần này lấy secret key từ tệp môi trường (env hoặc properties).

# 2. Screenshot khởi động tính năng Auth

![Màn hình Termianl](./imgs/auth/cmd1.png)
![](./imgs/auth/cmd2.png)
![](./imgs/auth/cmd4.png)
![](./imgs/auth/cmd5.png)
![](./imgs/auth/cmd6.png)
![](./imgs/auth/cmd7.png)
![](./imgs/auth/cmd8.png)
![](./imgs/auth/cmd9.png)


# 3. Xử Lý Ngoại Lệ Toàn Cục (Global Exception Handling)

Hệ thống sử dụng Global Exception Handler để bắt và chuẩn hóa toàn bộ phản hồi lỗi (Error Response) trả về cho phía Client. Dưới đây là kết quả thử nghiệm các trường hợp ngoại lệ trên Postman.

## 3.1. Kiểm tra ngoại lệ khi đăng nhập sai thông tin (404 Not Found / 401 Unauthorized)
* **Mô tả:** Hệ thống trả về lỗi khi người dùng nhập sai tài khoản hoặc mật khẩu không tồn tại.
* **Hình ảnh minh họa:**

![Kết quả Postman - Lỗi đăng nhập không tìm thấy tài khoản](./imgs/auth/exception_notfound.png)

---

## 3.2. Kiểm tra tính hợp lệ của dữ liệu đầu vào (400 Bad Request)
* **Mô tả:** Hệ thống tự động bắt lỗi và trả về danh sách các trường dữ liệu không thỏa mãn điều kiện validation (ví dụ: để trống email, mật khẩu quá ngắn...).
* **Hình ảnh minh họa:**

![Kết quả Postman - Lỗi Validation dữ liệu đầu vào](./imgs/auth/exception_badrequest.png)

# 2. Database Schema Documentation

## 1. Cở sở dữ liệu

Cơ sở dữ liệu được xây dựng bằng PostgreSQL để quản lý dữ liệu cho hệ thống thương mại điện tử.

Database gồm 13 bảng:

| STT | Bảng | Mô tả |
|---:|---|---|
| 1 | `roles` | Quản lý vai trò người dùng |
| 2 | `users` | Quản lý tài khoản người dùng |
| 3 | `categories` | Quản lý danh mục sản phẩm |
| 4 | `products` | Quản lý thông tin sản phẩm |
| 5 | `images` | Quản lý hình ảnh sản phẩm |
| 6 | `provinces` | Quản lý tỉnh/thành phố |
| 7 | `wards` | Quản lý phường |
| 8 | `receivers` | Quản lý người nhận hàng |
| 9 | `statuses` | Quản lý trạng thái đơn hàng |
| 10 | `orders` | Quản lý đơn hàng |
| 11 | `order_details` | Quản lý chi tiết đơn hàng |
| 12 | `payment_statuses` | Quản lý trạng thái thanh toán |
| 13 | `payments` | Quản lý thông tin thanh toán |

## 2. Chi tiết các bảng

### 2.1. Bảng `roles` — Vai trò người dùng

Lưu trữ các vai trò được sử dụng để phân quyền trong hệ thống.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã vai trò |
| `name` | VARCHAR(100) | | Tên vai trò, ví dụ ADMIN, USER |
| `description` | TEXT | | Mô tả vai trò |

### 2.2. Bảng `users` — Tài khoản người dùng

Lưu trữ thông tin tài khoản của khách hàng và quản trị viên.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã người dùng |
| `username` | VARCHAR(100) | | Tên đăng nhập |
| `phone` | VARCHAR(20) | | Số điện thoại |
| `email` | VARCHAR(255) | | Địa chỉ email |
| `password` | VARCHAR(255) | | Mật khẩu đã băm |
| `avatar` | VARCHAR(500) | | Đường dẫn ảnh đại diện |
| `role_id` | INTEGER | FK | Mã vai trò, tham chiếu `roles.id` |
| `created_at` | TIMESTAMP | | Thời điểm tạo tài khoản |
| `deleted_at` | TIMESTAMP | | Thời điểm xóa tài khoản |
| `edited_at` | TIMESTAMP | | Thời điểm chỉnh sửa tài khoản |

### 2.3. Bảng `categories` — Danh mục sản phẩm

Lưu trữ các danh mục dùng để phân loại sản phẩm.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã danh mục |
| `name` | VARCHAR(255) | | Tên danh mục, ví dụ WATCH, PHONE, LAPTOP |
| `description` | TEXT | | Mô tả danh mục |

### 2.4. Bảng `products` — Sản phẩm

Lưu trữ thông tin chi tiết của các sản phẩm được kinh doanh.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã sản phẩm |
| `name` | VARCHAR(255) | | Tên sản phẩm |
| `price` | INTEGER | | Giá gốc của sản phẩm, đơn vị VND |
| `thumbnail` | VARCHAR(500) | | Đường dẫn ảnh đại diện |
| `weight` | INTEGER | | Khối lượng sản phẩm (gram) |
| `description` | TEXT | | Mô tả chi tiết sản phẩm |
| `discount` | INTEGER | | Phần trăm giảm giá |
| `total_price` | INTEGER | | Giá bán sau khi giảm |
| `category_id` | INTEGER | FK | Mã danh mục, tham chiếu `categories.id` |
| `inventory_quantity` | INTEGER | | Số lượng tồn kho |
| `sold_quantity` | INTEGER | | Số lượng đã bán |
| `created_at` | TIMESTAMP | | Thời điểm tạo sản phẩm |
| `deleted_at` | TIMESTAMP | | Thời điểm xóa sản phẩm |
| `edited_at` | TIMESTAMP | | Thời điểm chỉnh sửa sản phẩm |
| `is_deleted` | BOOLEAN | Default FALSE | Đánh dấu sản phẩm đã bị xóa hay chưa |

### 2.5. Bảng `images` — Hình ảnh sản phẩm

Lưu trữ các hình ảnh liên quan đến sản phẩm.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã hình ảnh |
| `product_id` | INTEGER | FK | Mã sản phẩm, tham chiếu `products.id` |
| `path` | VARCHAR(500) | | Đường dẫn đến tệp hình ảnh |
| `alt` | VARCHAR(255) | | Văn bản thay thế của hình ảnh |

### 2.6. Bảng `provinces` — Tỉnh/thành phố

Lưu trữ thông tin tỉnh hoặc thành phố.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã tỉnh/thành phố |
| `name` | VARCHAR(255) | | Tên tỉnh/thành phố |

### 2.7. Bảng `wards` — Phường

Lưu trữ danh sách phường và tỉnh/thành phố tương ứng.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã phường |
| `name` | VARCHAR(255) | | Tên phường |
| `province_id` | INTEGER | FK | Mã tỉnh/thành phố, tham chiếu `provinces.id` |

### 2.8. Bảng `receivers` — Người nhận hàng

Lưu trữ thông tin người nhận và địa chỉ giao hàng.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã người nhận |
| `fullname` | VARCHAR(255) | | Họ và tên người nhận |
| `phone` | VARCHAR(20) | | Số điện thoại người nhận |
| `ward_id` | INTEGER | FK | Mã phường, tham chiếu `wards.id` |
| `created_at` | TIMESTAMP | | Thời điểm tạo thông tin |
| `deleted_at` | TIMESTAMP | | Thời điểm xóa thông tin |
| `edited_at` | TIMESTAMP | | Thời điểm chỉnh sửa thông tin |

### 2.9. Bảng `statuses` — Trạng thái đơn hàng

Lưu trữ danh sách trạng thái xử lý đơn hàng.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã trạng thái |
| `name` | VARCHAR(100) | | Tên trạng thái |
| `description` | TEXT | | Mô tả trạng thái |

Các giá trị mẫu:

- `PENDING`: Đơn hàng đang chờ xác nhận.
- `CONFIRMED`: Đơn hàng đã được xác nhận.
- `SHIPPING`: Đơn hàng đang được giao.
- `COMPLETED`: Đơn hàng đã hoàn thành.
- `CANCELLED`: Đơn hàng đã bị hủy.

### 2.10. Bảng `orders` — Đơn hàng

Lưu trữ thông tin tổng quan của đơn hàng do người dùng tạo.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã đơn hàng |
| `receiver_id` | INTEGER | FK | Mã người nhận, tham chiếu `receivers.id` |
| `user_id` | INTEGER | FK | Mã người đặt hàng, tham chiếu `users.id` |
| `price` | NUMERIC(12,2) | | Giá trị tiền hàng (vnđ) |
| `total_price` | NUMERIC(12,2) | | Tổng tiền đơn hàng |
| `status_id` | INTEGER | FK | Trạng thái đơn hàng, tham chiếu `statuses.id` |
| `payment_id` | INTEGER | FK, UNIQUE | Mã thanh toán, tham chiếu `payments.id` |
| `created_at` | TIMESTAMP | | Thời điểm tạo đơn hàng |
| `deleted_at` | TIMESTAMP | | Thời điểm xóa đơn hàng |
| `edited_at` | TIMESTAMP | | Thời điểm chỉnh sửa đơn hàng |

### 2.11. Bảng `order_details` — Chi tiết đơn hàng

Lưu trữ các sản phẩm được đặt trong từng đơn hàng.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã chi tiết đơn hàng |
| `product_id` | INTEGER | FK | Mã sản phẩm, tham chiếu `products.id` |
| `order_id` | INTEGER | FK | Mã đơn hàng, tham chiếu `orders.id` |
| `price` | NUMERIC(12,2) | | Đơn giá sản phẩm tại thời điểm đặt hàng |
| `quantity` | INTEGER | | Số lượng sản phẩm được đặt |
| `total_price` | NUMERIC(12,2) | | Thành tiền của dòng chi tiết |

### 2.12. Bảng `payment_statuses` — Trạng thái thanh toán

Lưu trữ các trạng thái thanh toán của giao dịch.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã trạng thái thanh toán |
| `name` | VARCHAR(100) | | Tên trạng thái |
| `description` | TEXT | | Mô tả trạng thái |

Các giá trị mẫu:

- `UNPAID`: Chưa thanh toán.
- `PAID`: Đã thanh toán.
- `FAILED`: Thanh toán thất bại.
- `REFUNDED`: Đã hoàn tiền.

### 2.13. Bảng `payments` — Thanh toán

Lưu trữ thông tin giao dịch thanh toán của đơn hàng.

| Thuộc tính | Kiểu dữ liệu | Ràng buộc | Mô tả |
|---|---|---|---|
| `id` | INTEGER | PK, Identity | Mã thanh toán |
| `payment_status_id` | INTEGER | FK | Trạng thái thanh toán, tham chiếu `payment_statuses.id` |
| `method` | VARCHAR(100) | | Phương thức thanh toán |
| `amount` | INTEGER | | Số tiền thanh toán, đơn vị VND |
| `transaction_id` | VARCHAR(255) | | Mã giao dịch từ hệ thống thanh toán |
| `created_at` | TIMESTAMP | | Thời điểm tạo giao dịch |

## 3. Quan hệ giữa các bảng

| Bảng nguồn | Khóa ngoại | Bảng đích | Quan hệ |
|---|---|---|---|
| `users` | `role_id` | `roles.id` | Nhiều người dùng thuộc một vai trò |
| `products` | `category_id` | `categories.id` | Nhiều sản phẩm thuộc một danh mục |
| `images` | `product_id` | `products.id` | Nhiều hình ảnh thuộc một sản phẩm |
| `wards` | `province_id` | `provinces.id` | Nhiều phường thuộc một tỉnh/thành phố |
| `receivers` | `ward_id` | `wards.id` | Nhiều người nhận có thể cùng thuộc một phường |
| `orders` | `user_id` | `users.id` | Một người dùng có thể tạo nhiều đơn hàng |
| `orders` | `receiver_id` | `receivers.id` | Một người nhận có thể xuất hiện trong nhiều đơn hàng |
| `orders` | `status_id` | `statuses.id` | Nhiều đơn hàng có thể cùng trạng thái |
| `orders` | `payment_id` | `payments.id` | Đơn hàng liên kết tối đa một bản ghi thanh toán |
| `payments` | `payment_status_id` | `payment_statuses.id` | Nhiều giao dịch có thể cùng trạng thái thanh toán |
| `order_details` | `order_id` | `orders.id` | Một đơn hàng có nhiều dòng chi tiết |
| `order_details` | `product_id` | `products.id` | Một sản phẩm có thể xuất hiện trong nhiều dòng chi tiết |
