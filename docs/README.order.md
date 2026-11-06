# Hướng dẫn tính năng Đơn hàng

Tài liệu này trình bày đầy đủ tính năng Đơn hàng trong dự án e-commerce Spring Boot: kiến trúc, các tệp được thêm hoặc hoàn thiện, quan hệ dữ liệu hiện có, luồng nghiệp vụ, kiến thức kỹ thuật, giải thích mã nguồn, cách kiểm thử API và các tình huống kiểm thử bằng terminal.

## 1. Phạm vi tính năng

Tính năng hỗ trợ:

- Lấy danh sách đơn hàng chưa bị xóa.
- Lấy chi tiết một đơn hàng theo ID.
- Tạo đơn hàng cho người dùng và người nhận đã tồn tại.
- Thêm một hoặc nhiều sản phẩm hiện có vào đơn hàng.
- Tính giá hoàn toàn ở phía server.
- Giảm tồn kho và tăng số lượng đã bán.
- Cập nhật trạng thái bằng các trạng thái đã có trong hệ thống.
- Xóa mềm thông qua cột `deleted_at` hiện có.

Tính năng không thay đổi mô hình cơ sở dữ liệu. Toàn bộ xử lý sử dụng các bảng `orders`, `order_details`, `users`, `receivers`, `products` và `statuses` đã được tạo bởi Flyway.

## 2. Kiến trúc

```text
HTTP request
    |
    v
OrderController
    |
    v
IOrder
    |
    v
OrderService  -- nghiệp vụ và transaction
    |
    +--> OrderRepository
    +--> OrderDetailRepository
    +--> UserRepository
    +--> ReceiverRepository
    +--> ProductRepository
    +--> StatusRepository
    |
    v
JPA entities và PostgreSQL
    |
    v
OrderMapper --> OrderDTO --> JSON response
```

Vai trò của từng tầng:

- Controller xử lý HTTP, đường dẫn, request body và status code.
- Interface định nghĩa hợp đồng của tính năng.
- Service chứa nghiệp vụ và ranh giới transaction.
- Repository truy cập cơ sở dữ liệu bằng Spring Data JPA.
- Mapper chuyển entity sang DTO trả về client.

## 3. Các tệp được thêm hoặc hoàn thiện

### Tệp mới

| Tệp | Trách nhiệm |
| --- | --- |
| `interfaces/order/IOrder.java` | Khai báo các thao tác công khai của Order service. |
| `services/order/OrderService.java` | Truy vấn, tạo, đổi trạng thái, cập nhật tồn kho và xóa mềm đơn hàng. |
| `repositories/OrderDetailRepository.java` | Đọc và ghi bảng `order_details`. |
| `repositories/ReceiverRepository.java` | Tìm người nhận hiện có theo ID. |
| `dtos/order/OrderStatusRequest.java` | Request body có validation dùng khi đổi trạng thái. |
| `controllers/OrderControllerTest.java` | Kiểm thử endpoint và validation của controller. |

### Tệp scaffold được hoàn thiện hoặc sửa

| Tệp | Nội dung thay đổi |
| --- | --- |
| `controllers/OrderController.java` | Thêm năm REST endpoint. |
| `mappers/OrderMapper.java` | Ánh xạ đơn hàng, người nhận và chi tiết sản phẩm. |
| `repositories/OrderRepository.java` | Thêm truy vấn đơn đang hoạt động và tra cứu theo payment. |
| `dtos/order/OrderDTO.java` | Sửa package và thêm validation. |
| `dtos/order/OrderDetailDTO.java` | Validation cho sản phẩm và số lượng. |
| `repositories/UserRepository.java` | Sửa kiểu ID thành `Integer`. |
| `repositories/StatusRepository.java` | Sửa kiểu ID và thêm tìm trạng thái không phân biệt hoa thường. |
| `exception/GlobalExceptionHandler.java` | Xử lý lỗi nghiệp vụ thành HTTP 400. |

Tệp rỗng `interfaces/product/IOrder.java` được thay bằng `interfaces/order/IOrder.java` đúng trách nhiệm hơn.

## 4. Mô hình dữ liệu hiện có

Không có entity hoặc migration mới.

```text
users      1 ---- * orders
receivers  1 ---- * orders
statuses   1 ---- * orders
orders     1 ---- * order_details
products   1 ---- * order_details
```

Một `Order` tham chiếu người dùng, người nhận, trạng thái và payment tùy chọn. Mỗi `OrderDetail` tham chiếu một đơn hàng và một sản phẩm.

### Ý nghĩa các trường giá

- `products.price`: giá gốc của sản phẩm.
- `products.total_price`: giá hiệu lực sau giảm giá nếu có.
- `order_details.price`: đơn giá được ghi nhận tại thời điểm đặt hàng.
- `order_details.total_price`: `đơn giá × số lượng`.
- `orders.price` và `orders.total_price`: tổng các dòng chi tiết trong phiên bản cơ bản này.

Việc lưu đơn giá tại `order_details` giúp đơn hàng lịch sử không bị thay đổi khi giá sản phẩm được cập nhật sau này.

## 5. Danh sách API

| Method | Endpoint | Chức năng | Thành công |
| --- | --- | --- | --- |
| `GET` | `/api/v1/orders` | Danh sách đơn đang hoạt động | `200 OK` |
| `GET` | `/api/v1/orders/{id}` | Chi tiết một đơn | `200 OK` |
| `POST` | `/api/v1/orders` | Tạo đơn hàng | `201 Created` |
| `PATCH` | `/api/v1/orders/{id}/status` | Đổi trạng thái | `200 OK` |
| `DELETE` | `/api/v1/orders/{id}` | Xóa mềm | `204 No Content` |

## 6. Giải thích mã nguồn

### 6.1 Validation DTO

`OrderDTO` được dùng làm cấu trúc request và response phù hợp với phạm vi dự án cơ bản.

Khi tạo đơn:

- `userId` bắt buộc.
- `receiver` bắt buộc và phải có `id` tồn tại.
- `products` phải có ít nhất một phần tử.
- Mỗi phần tử phải có `productId`.
- `quantity` phải lớn hơn hoặc bằng `1`.

Các trường `id`, `username`, `totalAmount` và `createdAt` là dữ liệu response. Server tự tải hoặc tính chúng. Giá trị tổng do client gửi lên không được tin cậy.

### 6.2 Controller

`OrderController` có base path `/api/v1/orders` và chỉ chuyển yêu cầu cho `IOrder`.

- `@Valid` kích hoạt Jakarta Bean Validation.
- `POST` trả `201 Created`.
- `DELETE` trả response rỗng `204 No Content`.
- `PATCH` phù hợp với thao tác chỉ thay đổi trạng thái.

### 6.3 Service và transaction

Các thao tác đọc sử dụng:

```java
@Transactional(readOnly = true)
```

Transaction đọc giúp giữ phiên JPA trong lúc mapper truy cập các quan hệ lazy và thể hiện rõ rằng phương thức không sửa dữ liệu.

Các thao tác tạo, cập nhật và xóa sử dụng `@Transactional`. Nếu tìm sản phẩm, kiểm tra tồn kho, lưu đơn hoặc lưu chi tiết bị lỗi, Spring rollback toàn bộ. Nhờ đó không xuất hiện đơn thiếu chi tiết hoặc tồn kho chỉ bị cập nhật một phần.

### 6.4 Luồng tạo đơn hàng

`createOrder` thực hiện:

1. Tìm người dùng bằng `userId`.
2. Kiểm tra và tìm `receiver.id`.
3. Dùng trạng thái được yêu cầu hoặc mặc định `PENDING`.
4. Khởi tạo đối tượng `Order` chưa lưu.
5. Với từng sản phẩm:
   - Tìm sản phẩm.
   - Kiểm tra tồn kho.
   - Dùng `product.totalPrice` nếu có, nếu không dùng `product.price`.
   - Tính subtotal.
   - Tạo `OrderDetail` liên kết với order.
   - Giảm `inventoryQuantity`.
   - Tăng `soldQuantity`.
6. Cộng tất cả subtotal.
7. Lưu order để nhận ID tự sinh.
8. Lưu toàn bộ order details.
9. Ánh xạ kết quả sang `OrderDTO`.

Server luôn tự tính tổng từ dữ liệu sản phẩm đáng tin cậy. Đây là nguyên tắc bảo mật quan trọng, tránh việc client tự sửa giá.

### 6.5 Luồng truy vấn

Danh sách sử dụng method Spring Data:

```java
findAllByDeletedAtIsNullOrderByCreatedAtDesc()
```

Tên method được Spring diễn giải thành:

- Chỉ lấy bản ghi có `deleted_at IS NULL`.
- Sắp xếp đơn mới nhất trước.

Mỗi đơn sau đó tải chi tiết bằng `findAllByOrderIdOrderById`. Mapper đọc thêm user, status, receiver, ward, province, product và image trong transaction đọc.

### 6.6 Cập nhật trạng thái

Service tìm trạng thái bằng `findByNameIgnoreCase`, vì vậy `confirmed` và `CONFIRMED` cho cùng kết quả.

Trạng thái phải tồn tại trong bảng `statuses`:

- `PENDING`
- `CONFIRMED`
- `SHIPPING`
- `COMPLETED`
- `CANCELLED`

Thời điểm sửa được lưu vào `edited_at`.

### 6.7 Xóa mềm

Xóa đơn chỉ gán `deleted_at`, không xóa vật lý. API danh sách và chi tiết không trả về đơn đã xóa.

Cách này giữ lịch sử và các khóa ngoại. Phiên bản hiện tại không hoàn tồn kho khi xóa mềm. Hoàn tồn kho nên thuộc nghiệp vụ hủy đơn hoặc hoàn tiền riêng.

### 6.8 Mapper

`OrderMapper` tạo response gồm:

- ID, user, trạng thái, tổng tiền và thời điểm tạo.
- Tên, điện thoại, phường và tỉnh của người nhận.
- ID, tên, ảnh, số lượng, đơn giá và subtotal của sản phẩm.

Mô hình hiện có không có cột địa chỉ đường/phố nên `receiver.address` trả về `null`. Không thêm cột mới chỉ để lấp trường này.

Ảnh ưu tiên `thumbnail`; nếu không có thì lấy ảnh đầu tiên trong danh sách image.

### 6.9 Xử lý lỗi

Không tìm thấy tài nguyên, HTTP `404`:

```json
{
  "error": "Not Found",
  "message": "Không tìm thấy đơn hàng"
}
```

Lỗi nghiệp vụ, HTTP `400`:

```json
{
  "error": "Bad Request",
  "message": "Sản phẩm iPhone 16 không đủ tồn kho"
}
```

Lỗi validation, HTTP `400`:

```json
{
  "products": "Đơn hàng phải có ít nhất một sản phẩm"
}
```

## 7. Chạy dự án

Từ thư mục gốc:

```bash
docker start cnlt-hd-t7-postgres
```

```bash
DB_LOCAL=ecommerce \
DB_USERNAME_LOCAL=ecommerce \
DB_PASSWORD_LOCAL=ecommerce_local \
JWT_SECRET_KEY=0123456789abcdef0123456789abcdef0123456789abcdef \
mvn spring-boot:run
```

API chạy tại `http://localhost:8080`.

## 8. Kiểm thử API bằng curl

Các ví dụ dùng ID seed trong `V2__seed_data.sql`.

### 8.1 Danh sách đơn hàng

```bash
curl --request GET \
  --url http://localhost:8080/api/v1/orders
```

Nếu có `jq`:

```bash
curl --silent http://localhost:8080/api/v1/orders | jq
```

### 8.2 Chi tiết một đơn

```bash
curl --request GET \
  --url http://localhost:8080/api/v1/orders/1
```

Kết quả mong đợi: HTTP `200`, có receiver và danh sách sản phẩm.

### 8.3 Tạo đơn

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/orders \
  --header 'Content-Type: application/json' \
  --data '{
    "userId": 2,
    "receiver": {"id": 1},
    "products": [
      {"productId": 1, "quantity": 1},
      {"productId": 3, "quantity": 2}
    ]
  }'
```

Kết quả mong đợi: HTTP `201`, trạng thái mặc định `PENDING`, tổng tiền do server tính.

Lưu ID mới bằng `jq`:

```bash
ORDER_ID=$(
  curl --silent --request POST \
    --url http://localhost:8080/api/v1/orders \
    --header 'Content-Type: application/json' \
    --data '{
      "userId": 2,
      "receiver": {"id": 1},
      "products": [{"productId": 1, "quantity": 1}]
    }' | jq --raw-output '.id'
)

echo "$ORDER_ID"
```

Tạo đơn sẽ thay đổi tồn kho và số lượng đã bán. Nên dùng số lượng nhỏ khi kiểm thử.

### 8.4 Tạo đơn với trạng thái cụ thể

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/orders \
  --header 'Content-Type: application/json' \
  --data '{
    "userId": 3,
    "status": "PENDING",
    "receiver": {"id": 2},
    "products": [{"productId": 2, "quantity": 1}]
  }'
```

### 8.5 Đổi trạng thái

```bash
curl --request PATCH \
  --url http://localhost:8080/api/v1/orders/4/status \
  --header 'Content-Type: application/json' \
  --data '{"status":"CONFIRMED"}'
```

Thay `4` bằng ID thực tế vừa tạo.

### 8.6 Xóa mềm

```bash
curl --include --request DELETE \
  --url http://localhost:8080/api/v1/orders/4
```

Kết quả: `204 No Content`.

Kiểm tra lại:

```bash
curl --include http://localhost:8080/api/v1/orders/4
```

Kết quả: `404 Not Found`.

### 8.7 Kiểm thử validation

Danh sách sản phẩm rỗng:

```bash
curl --include --request POST \
  --url http://localhost:8080/api/v1/orders \
  --header 'Content-Type: application/json' \
  --data '{
    "userId": 2,
    "receiver": {"id": 1},
    "products": []
  }'
```

Số lượng không hợp lệ:

```bash
curl --include --request POST \
  --url http://localhost:8080/api/v1/orders \
  --header 'Content-Type: application/json' \
  --data '{
    "userId": 2,
    "receiver": {"id": 1},
    "products": [{"productId": 1, "quantity": 0}]
  }'
```

Không đủ tồn kho:

```bash
curl --include --request POST \
  --url http://localhost:8080/api/v1/orders \
  --header 'Content-Type: application/json' \
  --data '{
    "userId": 2,
    "receiver": {"id": 1},
    "products": [{"productId": 1, "quantity": 999999}]
  }'
```

## 9. Kiểm thử tự động

```bash
DB_LOCAL=ecommerce \
DB_USERNAME_LOCAL=ecommerce \
DB_PASSWORD_LOCAL=ecommerce_local \
JWT_SECRET_KEY=0123456789abcdef0123456789abcdef0123456789abcdef \
mvn test
```

Chỉ chạy test Order:

```bash
mvn -Dtest=OrderControllerTest test
```

Test bao phủ danh sách, chi tiết, tạo hợp lệ, tạo không hợp lệ, cập nhật trạng thái và xóa.

## 10. Giới hạn hiện tại

Đây là tính năng cơ bản phục vụ học Spring. Phạm vi hiện tại chưa có:

- Kiểm tra quyền sở hữu đơn theo tài khoản đăng nhập.
- Phân trang và bộ lọc.
- Sửa danh sách sản phẩm sau khi tạo.
- Hoàn tồn kho khi hủy hoặc xóa.
- Lock để xử lý nhiều giao dịch mua đồng thời.
- DTO request và response riêng biệt.
- Phí vận chuyển hoặc khuyến mãi phức tạp.

Các phần này có thể được bổ sung sau mà không làm thay đổi luồng cơ bản đã mô tả.
