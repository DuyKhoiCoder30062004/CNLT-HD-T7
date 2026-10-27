# 1. Ecommerce REST API Documentation

## 1. Tổng quan

Hệ thống Ecommerce cung cấp RESTful API phục vụ xác thực người dùng, quản lý sản phẩm và xử lý đơn hàng. Backend được xây dựng bằng Spring Boot, sử dụng Spring Security để xác thực JWT, Spring Data JPA để truy cập cơ sở dữ liệu PostgreSQL và DTO để trao đổi dữ liệu giữa Client và Server.

**Base URL:** `http://localhost:8080`


### Quy ước chung

- Dữ liệu request và response sử dụng JSON.
- Các API yêu cầu xác thực sử dụng header `Authorization: Bearer <JWT>`.
- Client chỉ gửi dữ liệu cần thiết thông qua DTO, không gửi trực tiếp Entity của hệ thống.
- Backend chịu trách nhiệm xác thực dữ liệu, kiểm tra quyền, tính toán giá trị nghiệp vụ và bảo đảm tính toàn vẹn dữ liệu.
- Các API được mô tả dưới đây là hợp đồng API đề xuất; DTO và quyền cụ thể cần được đối chiếu với implementation thực tế.

---

## 2. Quyền truy cập API

| Nhóm API | Quyền đề xuất | Mô tả |
|---|---|---|
| Đăng nhập, đăng ký | Public | Không yêu cầu JWT |
| Thông tin người dùng hiện tại | Authenticated | Yêu cầu JWT hợp lệ |
| Xem danh sách sản phẩm | Public | Cho phép khách truy cập |
| Xem chi tiết sản phẩm | Public | Cho phép khách truy cập |
| Tạo sản phẩm | ADMIN | Chỉ quản trị viên |
| Cập nhật sản phẩm | ADMIN | Chỉ quản trị viên |
| Xóa sản phẩm | ADMIN | Chỉ quản trị viên |
| Tạo đơn hàng | Authenticated | Người dùng phải đăng nhập |
| Xem danh sách đơn hàng | Authenticated | Chỉ trả về các đơn hàng người dùng được phép xem |
| Xem chi tiết đơn hàng | Authenticated | Kiểm tra chủ sở hữu hoặc quyền ADMIN |
| Cập nhật trạng thái đơn hàng | ADMIN | Không cho phép khách hàng tự ý thay đổi trạng thái |

**Lưu ý triển khai:** Trong `SecurityConfig` hiện tại, `.anyRequest().permitAll()` đang cho phép các endpoint khác truy cập mà không yêu cầu xác thực. Vì vậy, bảng trên là quyền truy cập đề xuất, chưa phải quyền đã được bảo đảm cho toàn bộ API. Cần cấu hình `requestMatchers` và kiểm tra quyền sở hữu đơn hàng trong backend.

---

## 3. API Authentication

### 3.1. Đăng nhập

**Endpoint:** `POST /api/auth/login`

| Thuộc tính | Mô tả |
|---|---|
| HTTP method | POST |
| Path | `/api/auth/login` |
| Quyền | Public |
| Request DTO | `LoginRequest` |
| Response DTO | `LoginResponse` |
| Content-Type | `application/json` |


**Xử lý nghiệp vụ:**

1. Kiểm tra dữ liệu đầu vào.
2. Tìm người dùng theo số điện thoại.
3. So sánh mật khẩu gửi lên với mật khẩu đã mã hóa bằng BCrypt.
4. Nếu thông tin không hợp lệ, trả về thông báo đăng nhập chung.
5. Nếu hợp lệ, tạo JWT và trả thông tin người dùng cùng token.


**Các lỗi có thể xảy ra:**

| HTTP status | Trường hợp |
|---|---|
| 400 Bad Request | Request thiếu trường bắt buộc hoặc dữ liệu không hợp lệ |
| 401 Unauthorized | Số điện thoại không tồn tại hoặc mật khẩu sai |
| 500 Internal Server Error | Lỗi hệ thống ngoài dự kiến |

Không sử dụng hai thông báo khác nhau cho trường hợp tài khoản không tồn tại và mật khẩu sai, nhằm hạn chế việc dò tìm tài khoản hợp lệ.

### 3.2. Đăng ký tài khoản

**Endpoint:** `POST /api/auth/register`

| Thuộc tính | Mô tả |
|---|---|
| HTTP method | POST |
| Path | `/api/auth/register` |
| Quyền | Public |
| Request DTO | `RegisterRequest` |
| Response DTO | `LoginResponse` theo implementation hiện tại |

**Xử lý nghiệp vụ:**

1. Kiểm tra tính hợp lệ của username, email, số điện thoại và mật khẩu.
2. Kiểm tra username và email đã tồn tại hay chưa.
3. Mã hóa mật khẩu trước khi lưu.
4. Tạo tài khoản mới.
5. Theo implementation hiện tại, gọi quy trình đăng nhập để tạo JWT và trả kết quả.

**Các lỗi có thể xảy ra:**

| HTTP status | Trường hợp |
|---|---|
| 400 Bad Request | Dữ liệu đăng ký không hợp lệ |
| 409 Conflict | Username hoặc email đã tồn tại |
| 500 Internal Server Error | Lỗi hệ thống ngoài dự kiến |

### 3.3. Lấy thông tin người dùng hiện tại

**Endpoint:** `GET /api/auth/me`

| Thuộc tính | Mô tả |
|---|---|
| HTTP method | GET |
| Path | `/api/auth/me` |
| Quyền | Authenticated |
| Request DTO | Không có body |
| Response DTO | `LoginResponse` theo implementation hiện tại |
| Header | `Authorization: Bearer <JWT>` |

**Xử lý nghiệp vụ:**

1. JWT filter đọc token từ header.
2. Kiểm tra chữ ký và thời hạn token.
3. Trích xuất subject, tương ứng số điện thoại theo thiết kế hiện tại.
4. Đưa thông tin xác thực vào `SecurityContextHolder`.
5. Controller gọi service để truy vấn thông tin người dùng.
6. Trả dữ liệu người dùng về Client.

**Các lỗi có thể xảy ra:**

| HTTP status | Trường hợp |
|---|---|
| 401 Unauthorized | Thiếu JWT, token không hợp lệ hoặc hết hạn |
| 404 Not Found | Không tìm thấy người dùng tương ứng trong database |
| 500 Internal Server Error | Lỗi hệ thống ngoài dự kiến |

---

## 4. API quản lý sản phẩm

### 4.1. Danh sách endpoint

| HTTP method | Path | Quyền đề xuất | Mục đích |
|---|---|---|---|
| GET | `/api/products` | Public | Lấy danh sách sản phẩm |
| GET | `/api/products/{id}` | Public | Lấy chi tiết một sản phẩm |
| POST | `/api/products` | ADMIN | Tạo sản phẩm |
| PUT | `/api/products/{id}` | ADMIN | Cập nhật sản phẩm |
| DELETE | `/api/products/{id}` | ADMIN | Xóa hoặc đánh dấu xóa sản phẩm |

### 4.2. DTO

**`ProductDTO`** được sử dụng để trao đổi dữ liệu sản phẩm giữa Client và Server. Theo thiết kế hiện tại, DTO có các trường như:

- `id`: mã sản phẩm.
- `name`: tên sản phẩm.
- `price`: giá bán.
- `description`: mô tả.
- `discount`: mức giảm giá.
- `inventoryQuantity`: số lượng tồn kho.
- `soldQuantity`: số lượng đã bán.
- `categoryName`: tên danh mục.
- `imagePaths`: danh sách đường dẫn hình ảnh.

Các trường thực tế cần tuân theo class `ProductDTO` hiện có. Đối với thao tác tạo và cập nhật, có thể sử dụng DTO riêng như `CreateProductRequest` và `UpdateProductRequest` để tránh cho Client tự điều chỉnh các trường hệ thống như `id` hoặc số lượng đã bán.

### 4.3. Mô tả từng API

#### GET `/api/products`

Lấy danh sách sản phẩm đang được phép hiển thị.

- **Request:** Không yêu cầu body.
- **Response:** Danh sách `ProductDTO`.
- **200 OK:** Lấy danh sách thành công.
- **500 Internal Server Error:** Lỗi truy vấn hoặc lỗi hệ thống.

#### GET `/api/products/{id}`

Lấy thông tin chi tiết sản phẩm theo ID.

- **Request:** Truyền ID trên URL.
- **Response:** Một `ProductDTO`.
- **200 OK:** Tìm thấy sản phẩm.
- **404 Not Found:** Không tồn tại sản phẩm tương ứng.
- **500 Internal Server Error:** Lỗi hệ thống.

#### POST `/api/products`

Tạo sản phẩm mới.

**Request body minh họa:**

```json
{
  "name": "Smart Watch",
  "price": 1500000,
  "description": "Đồng hồ thông minh",
  "discount": 10,
  "inventoryQuantity": 50,
  "categoryName": "WATCH",
  "imagePaths": []
}
```

Đây là dữ liệu minh họa, cần điều chỉnh theo DTO thực tế.

- **201 Created** hoặc `200 OK`: Tạo thành công, tùy hợp đồng API đã triển khai.
- **400 Bad Request:** Dữ liệu không hợp lệ.
- **401 Unauthorized:** Chưa xác thực.
- **403 Forbidden:** Đã xác thực nhưng không có quyền ADMIN.
- **409 Conflict:** Vi phạm ràng buộc nghiệp vụ hoặc dữ liệu duy nhất, nếu có.
- **500 Internal Server Error:** Lỗi hệ thống.

Backend phải kiểm tra giá, danh mục và các ràng buộc liên quan trước khi lưu sản phẩm.

#### PUT `/api/products/{id}`

Cập nhật thông tin sản phẩm theo ID.

- **Request:** ID trên URL và dữ liệu cập nhật trong body.
- **200 OK:** Cập nhật thành công.
- **400 Bad Request:** Dữ liệu cập nhật không hợp lệ.
- **401 Unauthorized:** Chưa xác thực.
- **403 Forbidden:** Không có quyền cập nhật.
- **404 Not Found:** Không tìm thấy sản phẩm.
- **500 Internal Server Error:** Lỗi hệ thống.

Backend cần xác định rõ những trường nào được phép cập nhật, tránh để Client tùy ý thay đổi ID hoặc dữ liệu thống kê bán hàng.

#### DELETE `/api/products/{id}`

Xóa sản phẩm hoặc đánh dấu sản phẩm đã xóa theo chính sách hệ thống.

- **Request:** ID sản phẩm trên URL.
- **204 No Content:** Xóa thành công và không trả body.
- **401 Unauthorized:** Chưa xác thực.
- **403 Forbidden:** Không có quyền xóa.
- **404 Not Found:** Không tìm thấy sản phẩm.
- **409 Conflict:** Không thể xóa do ràng buộc nghiệp vụ, nếu hệ thống quy định như vậy.
- **500 Internal Server Error:** Lỗi hệ thống.

Nếu sản phẩm đã được sử dụng trong đơn hàng, nên cân nhắc soft delete hoặc chính sách lưu giữ lịch sử thay vì xóa vật lý làm mất tính toàn vẹn dữ liệu.

---

## 5. API quản lý đơn hàng

### 5.1. Danh sách endpoint

| HTTP method | Path | Quyền đề xuất | Mục đích |
|---|---|---|---|
| POST | `/api/orders` | Authenticated | Tạo đơn hàng |
| GET | `/api/orders` | Authenticated | Lấy danh sách đơn hàng được phép xem |
| GET | `/api/orders/{id}` | Authenticated | Lấy chi tiết đơn hàng |
| PUT | `/api/orders/update-status/{id}` | ADMIN được phân quyền | Cập nhật trạng thái đơn hàng |

### 5.2. DTO

Có thể sử dụng các DTO sau tùy theo implementation:

- `CreateOrderRequest`: thông tin cần thiết để tạo đơn hàng.
- `OrderDetailRequest`: ID sản phẩm và số lượng cần mua.
- `OrderDTO`: thông tin đơn hàng trả về.
- `OrderDetailDTO`: thông tin từng sản phẩm trong đơn hàng.
- `UpdateOrderStatusRequest`: trạng thái mới cần cập nhật.

Tên DTO và cấu trúc JSON cần khớp với các class thực tế trong dự án.

### 5.3. Mô tả từng API

#### POST `/api/orders`

Tạo đơn hàng mới cho người dùng đã đăng nhập.

**Request body minh họa:**

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**Luồng nghiệp vụ:**

1. Xác thực người dùng bằng JWT.
2. Lấy ID người dùng từ thông tin xác thực phía server, không tin tưởng `userId` do Client tự gửi.
3. Kiểm tra sản phẩm tồn tại và số lượng đặt mua hợp lệ.
4. Kiểm tra tồn kho và lấy giá sản phẩm từ database.
5. Tính tổng tiền ở backend.
6. Lưu bản ghi đơn hàng và các chi tiết đơn hàng.
7. Cập nhật tồn kho theo chính sách nghiệp vụ.
8. Commit transaction khi tất cả thao tác thành công.

**Response:** Thông tin đơn hàng đã tạo, thường sử dụng `OrderDTO`.

| HTTP status | Trường hợp |
|---|---|
| 200 OK | Tạo đơn hàng thành công |
| 400 Bad Request | Dữ liệu đặt hàng không hợp lệ |
| 401 Unauthorized | Chưa đăng nhập hoặc JWT không hợp lệ |
| 404 Not Found | Không tìm thấy sản phẩm được đặt |
| 409 Conflict | Không đủ tồn kho hoặc vi phạm điều kiện nghiệp vụ |
| 500 Internal Server Error | Lỗi hệ thống |

Nếu lưu đơn hàng hoặc một chi tiết đơn hàng thất bại, transaction phải rollback các thay đổi database liên quan. Nếu có thanh toán qua dịch vụ bên ngoài, cần xử lý riêng tính nhất quán giữa database và hệ thống thanh toán.

#### GET `/api/orders`

Lấy danh sách đơn hàng của người dùng hiện tại hoặc danh sách đơn hàng trong phạm vi quyền quản trị.

- **Request:** Không yêu cầu body.
- **Response:** Danh sách `OrderDTO`.
- **200 OK:** Truy vấn thành công.
- **401 Unauthorized:** Chưa đăng nhập.
- **403 Forbidden:** Không có quyền xem dữ liệu theo yêu cầu.
- **500 Internal Server Error:** Lỗi hệ thống.

Backend phải giới hạn dữ liệu theo người dùng hoặc vai trò. Không nên trả toàn bộ đơn hàng cho mọi tài khoản đã đăng nhập.

#### GET `/api/orders/{id}`

Lấy thông tin chi tiết của một đơn hàng.

- **Request:** ID đơn hàng trên URL.
- **Response:** `OrderDTO` cùng danh sách chi tiết đơn hàng nếu DTO được thiết kế như vậy.
- **200 OK:** Tìm thấy và có quyền truy cập.
- **401 Unauthorized:** Chưa đăng nhập.
- **403 Forbidden:** Đơn hàng thuộc người dùng khác và người gọi không có quyền xem.
- **404 Not Found:** Đơn hàng không tồn tại.
- **500 Internal Server Error:** Lỗi hệ thống.

Có thể trả `404` thay cho `403` trong một số thiết kế để hạn chế tiết lộ sự tồn tại của đơn hàng, nhưng cần thống nhất một chính sách cho toàn hệ thống.

#### PUT `/api/orders/update-status/{id}`

Cập nhật trạng thái đơn hàng.

**Request body minh họa:**

```json
{
  "status": "CONFIRMED"
}
```

Các trạng thái hợp lệ phải khớp danh mục trạng thái của hệ thống.

**Luồng nghiệp vụ:**

1. Xác thực người gọi.
2. Kiểm tra quyền cập nhật trạng thái.
3. Tìm đơn hàng theo ID.
4. Kiểm tra trạng thái hiện tại và tính hợp lệ của chuyển trạng thái.
5. Lưu trạng thái mới.
6. Trả thông tin đơn hàng đã cập nhật.

| HTTP status | Trường hợp |
|---|---|
| 200 OK | Cập nhật thành công |
| 400 Bad Request | Trạng thái gửi lên không hợp lệ |
| 401 Unauthorized | Chưa xác thực |
| 403 Forbidden | Không có quyền cập nhật trạng thái |
| 404 Not Found | Không tìm thấy đơn hàng |
| 409 Conflict | Chuyển trạng thái không hợp lệ theo nghiệp vụ |
| 500 Internal Server Error | Lỗi hệ thống |

---

## 6. Quy ước xử lý lỗi chung

Các API sử dụng mã HTTP để thể hiện kết quả xử lý:

| HTTP status | Ý nghĩa |
|---|---|
| 200 OK | Request thành công |
| 201 Created | Tạo tài nguyên thành công |
| 204 No Content | Thao tác thành công, không có response body |
| 400 Bad Request | Request không hợp lệ hoặc validation thất bại |
| 401 Unauthorized | Thiếu thông tin xác thực hoặc JWT không hợp lệ |
| 403 Forbidden | Không đủ quyền truy cập |
| 404 Not Found | Không tìm thấy tài nguyên |
| 409 Conflict | Xung đột dữ liệu hoặc điều kiện nghiệp vụ |
| 500 Internal Server Error | Lỗi hệ thống ngoài dự kiến |

Các lỗi validation có thể được xử lý bằng `MethodArgumentNotValidException`. Những lỗi nghiệp vụ như đăng nhập sai, tài nguyên không tồn tại và dữ liệu trùng lặp cần được ánh xạ sang exception phù hợp.

Ví dụ response lỗi:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "details": {
    "phone": "Số điện thoại không được để trống"
  }
}
```

Response thực tế phải thống nhất với `GlobalExceptionHandler`, `AuthenticationEntryPoint` và `AccessDeniedHandler` đang triển khai.

Đối với đăng nhập sai, hệ thống sử dụng cùng một thông báo chung cho tài khoản không tồn tại và mật khẩu không chính xác. Không trả stack trace, secret JWT hoặc thông tin database trong response cho Client.

---

## 7. Sơ đồ luồng nghiệp vụ thực tế

### 7.1. Luồng xác thực

```mermaid
flowchart TD
    A[Client] --> B[POST /api/auth/login]
    B --> C[Validate LoginRequest]
    C --> D{Tài khoản và mật khẩu hợp lệ?}
    D -- Không --> E[401 Unauthorized]
    D -- Có --> F[AuthService tạo JWT]
    F --> G[Trả LoginResponse]
    G --> H[Client gửi Bearer JWT]
    H --> I[JWT Filter xác minh token]
    I --> J{Token hợp lệ?}
    J -- Không --> K[401 Unauthorized]
    J -- Có --> L[Thiết lập SecurityContext]
    L --> M[GET /api/auth/me]
    M --> N[Truy vấn thông tin người dùng]
    N --> O[Trả dữ liệu người dùng]
```

### 7.2. Luồng tạo đơn hàng

```mermaid
flowchart TD
    A[Client gửi POST /api/orders] --> B[Xác thực JWT]
    B --> C{Người dùng hợp lệ?}
    C -- Không --> D[401 Unauthorized]
    C -- Có --> E[Kiểm tra sản phẩm và tồn kho]
    E --> F{Dữ liệu hợp lệ và đủ hàng?}
    F -- Không --> G[400 hoặc 409]
    F -- Có --> H[Bắt đầu transaction]
    H --> I[Tính tổng tiền phía server]
    I --> J[Lưu Order]
    J --> K[Lưu OrderDetail]
    K --> L[Cập nhật tồn kho nếu cần]
    L --> M{Tất cả thao tác thành công?}
    M -- Không --> N[Rollback transaction]
    M -- Có --> O[Commit transaction]
    O --> P[Trả 201 Created]
```

Các sơ đồ thể hiện luồng nghiệp vụ dự kiến.

---

## 8. Các nguyên tắc bảo đảm tính toàn vẹn dữ liệu

- ERD, từ điển dữ liệu, Entity JPA và schema PostgreSQL phải thống nhất về tên bảng, kiểu dữ liệu, khóa chính, khóa ngoại và unique constraints.
- Các ràng buộc quan trọng cần được bảo đảm trong database.
- Tạo đơn hàng, lưu chi tiết đơn hàng và cập nhật tồn kho cần nằm trong transaction phù hợp.
- Giá sản phẩm và tổng tiền phải được xác minh, tính toán ở backend.
- Quyền truy cập đơn hàng phải kiểm tra cả vai trò và quyền sở hữu dữ liệu.
- JWT HS256 bảo vệ chữ ký và tính toàn vẹn token, không mã hóa payload. Không đưa mật khẩu hoặc dữ liệu bí mật vào claims.
- Khi sử dụng Flyway, thay đổi schema được quản lý qua migration có phiên bản; `spring.jpa.hibernate.ddl-auto=validate` có thể dùng để kiểm tra sự tương thích giữa Entity và schema.

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


# 3. Tính năng: Authentication (Xác thực)

Tính năng này cung cấp xác thực người dùng và đăng ký mới.

* File cấu hình được lưu tại thư mục:
 `/docs/postman/ccnlthd - auth service.postman_collection.json`


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

