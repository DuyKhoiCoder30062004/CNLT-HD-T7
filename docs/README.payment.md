# Hướng dẫn tính năng thanh toán VNPay

Tài liệu này giải thích toàn bộ tính năng VNPay: lý thuyết cổng thanh toán, các tệp được thêm hoặc sửa, quy tắc bảo mật, luồng request và callback, chi tiết triển khai, cấu hình, kiểm thử sandbox thật và mô phỏng hoàn toàn bằng terminal.

## 1. Phạm vi tính năng

Tính năng cung cấp:

- Tạo URL thanh toán VNPay sandbox phiên bản `2.1.0`.
- Lưu payment bằng bảng `payments` hiện có.
- Liên kết payment với một đơn chưa có phương thức thanh toán.
- Xác minh Browser Return URL.
- Xác minh IPN server-to-server.
- Tạo và kiểm tra chữ ký HMAC-SHA512.
- So sánh số tiền callback với dữ liệu đã lưu.
- Cập nhật trạng thái `PAID` hoặc `FAILED`.
- Chuyển order từ `PENDING` sang `CONFIRMED` khi thành công.
- Xử lý IPN thành công lặp lại theo cách idempotent.

Không có entity, bảng, cột hoặc migration mới. Tính năng dùng lại `payments`, `payment_statuses`, `orders` và `statuses`.

## 2. Kiến trúc

```text
Client
  |
  | POST /api/v1/payments/vnpay/create
  v
VnPayController
  |
  v
IVnPay
  |
  v
VnPayService
  |              \
  |               --> VnPaySigner --> HMAC-SHA512
  |
  +--> OrderRepository
  +--> PaymentRepository
  +--> PaymentStatusRepository
  +--> StatusRepository
  |
  v
PostgreSQL

Trình duyệt ---- redirect ----> VNPay sandbox
Trình duyệt <--- Return URL ---- VNPay sandbox
Ứng dụng    <--- IPN callback -- VNPay server
```

## 3. Các tệp được thêm hoặc sửa

### Tệp production mới

| Tệp | Trách nhiệm |
| --- | --- |
| `config/VnPayConfig.java` | Nạp URL, Return URL, terminal code và secret từ cấu hình Spring. |
| `controllers/VnPayController.java` | Cung cấp endpoint create, Return và IPN. |
| `interfaces/payment/IVnPay.java` | Hợp đồng của payment service. |
| `services/payment/VnPayService.java` | Tạo payment, xác minh callback và cập nhật database. |
| `security/VnPaySigner.java` | Sắp xếp, URL encode, ký và xác minh tham số VNPay. |
| `repositories/PaymentRepository.java` | Lưu payment và tìm bằng merchant transaction reference. |
| `dtos/payment/VnPayCreateRequest.java` | Request có `orderId` và locale. |
| `dtos/payment/VnPayCreateResponse.java` | Trả payment ID, reference, status và redirect URL. |
| `dtos/payment/VnPayResultResponse.java` | Mô tả kết quả Browser Return. |
| `dtos/payment/VnPayIpnResponse.java` | Trả `RspCode` và `Message` đúng kiểu VNPay. |

### Tệp hiện có được sửa

| Tệp | Thay đổi |
| --- | --- |
| `repositories/OrderRepository.java` | Thêm truy vấn tìm order từ payment ID. |
| `repositories/PaymentStatusRepository.java` | Sửa kiểu ID và thêm tìm status không phân biệt hoa thường. |
| `resources/example.application.properties` | Thêm cấu hình sandbox an toàn, không chứa secret thật. |

### Test mới

| Tệp | Phạm vi |
| --- | --- |
| `VnPayControllerTest.java` | Hợp đồng HTTP cho create, Return và IPN. |
| `VnPayServiceTest.java` | Lưu payment và sinh URL sandbox có chữ ký. |
| `VnPaySignerTest.java` | Thứ tự tham số, HMAC-SHA512 chính xác và xác minh callback. |

## 4. Lý thuyết cổng thanh toán

### 4.1 Mô hình redirect

Ứng dụng không thu thập thông tin thẻ ngân hàng:

1. Backend tạo URL có chữ ký.
2. Frontend chuyển người dùng sang VNPay.
3. VNPay hiển thị giao diện và xử lý xác thực ngân hàng.
4. VNPay chuyển trình duyệt về Return URL.
5. Server VNPay gọi riêng IPN URL của ứng dụng.

Cách này giảm lượng dữ liệu thanh toán nhạy cảm mà dự án phải xử lý.

### 4.2 HMAC-SHA512

HMAC là cơ chế xác thực thông điệp. Nó chứng minh dữ liệu được ký bởi bên biết shared secret và dữ liệu đã ký không bị thay đổi.

HMAC không phải mã hóa. Các tham số URL vẫn đọc được. Secret không được đưa vào URL, source code, log hoặc frontend.

```text
Các tham số vnp_* đã sắp xếp và URL encode
                    +
              merchant secret
                    |
                    v
                HMAC-SHA512
                    |
                    v
              vnp_SecureHash
```

`VnPaySigner` loại `vnp_SecureHash` và `vnp_SecureHashType` trước khi tính lại chữ ký callback.

### 4.3 Quy tắc số tiền

VNPay yêu cầu `vnp_Amount` bằng số tiền VND nhân `100`.

```text
Số tiền lưu:       100.000 VND
Tham số VNPay:  10.000.000
```

Service nhân khi tạo URL và dùng cùng quy tắc khi kiểm tra callback.

### 4.4 Merchant transaction reference

`vnp_TxnRef` là mã tham chiếu phía merchant. Service tạo từ order ID và timestamp rồi lưu vào `payments.transaction_id`.

Giá trị này kết nối:

- Payment trong database.
- URL gửi sang VNPay.
- Return/IPN nhận từ VNPay.

### 4.5 Return URL và IPN

Return URL là redirect của trình duyệt, phù hợp để hiển thị kết quả. Tuy nhiên người dùng có thể đóng trình duyệt hoặc mất kết nối.

IPN là callback server-to-server đáng tin cậy hơn để cập nhật database. Trong production, IPN nên là kênh thông báo bất đồng bộ chính.

Dự án cơ bản này xác minh và xử lý cả hai. Hàm cập nhật có tính idempotent nên callback thành công lặp lại không cập nhật trạng thái nhiều lần.

### 4.6 Không tin callback trước khi xác minh chữ ký

Mọi người đều có thể tự tạo query string. Service kiểm tra theo thứ tự:

1. Xác minh `vnp_SecureHash`.
2. Tìm transaction bằng `vnp_TxnRef`.
3. So sánh `vnp_Amount` với payment đã lưu.
4. Đọc response code và transaction status.
5. Cập nhật database.

Ứng dụng không bao giờ đánh dấu đã thanh toán chỉ vì nhận `vnp_ResponseCode=00`.

## 5. Danh sách API

| Method | Endpoint | Chức năng |
| --- | --- | --- |
| `POST` | `/api/v1/payments/vnpay/create` | Tạo payment và URL redirect có chữ ký. |
| `GET` | `/api/v1/payments/vnpay/return` | Nhận và xác minh Browser Return. |
| `GET` | `/api/v1/payments/vnpay/ipn` | Nhận và xác minh thông báo server-to-server. |

## 6. Giải thích luồng mã nguồn

### 6.1 Cấu hình

```properties
vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
vnpay.return-url=${VNPAY_RETURN_URL:http://localhost:8080/api/v1/payments/vnpay/return}
vnpay.tmn-code=${VNPAY_TMN_CODE:}
vnpay.hash-secret=${VNPAY_HASH_SECRET:}
```

Sandbox URL có giá trị mặc định. Terminal code và secret không có credential mặc định. Endpoint create trả HTTP `400` cho đến khi hai biến được cấu hình.

### 6.2 Luồng tạo payment

`VnPayService.createPayment` thực hiện trong một transaction:

1. Kiểm tra terminal code và secret.
2. Chuẩn hóa locale thành `vn` hoặc `en`.
3. Tìm order chưa bị xóa.
4. Từ chối order đã có payment.
5. Kiểm tra mức tối thiểu 5.000 VND.
6. Tìm status `UNPAID` hiện có.
7. Tạo `Payment` có method `VNPAY`.
8. Lưu merchant transaction reference.
9. Liên kết payment vào `orders.payment_id`.
10. Tạo map tham số VNPay.
11. Sắp xếp và URL encode.
12. Ký HMAC-SHA512.
13. Trả URL đầy đủ.

Payment hết hạn sau 15 phút. Thời gian dùng múi giờ `Asia/Ho_Chi_Minh` và định dạng `yyyyMMddHHmmss`.

### 6.3 Tham số gửi VNPay

| Tham số | Giá trị |
| --- | --- |
| `vnp_Version` | `2.1.0` |
| `vnp_Command` | `pay` |
| `vnp_TmnCode` | Terminal code |
| `vnp_Amount` | Tổng tiền order nhân 100 |
| `vnp_CurrCode` | `VND` |
| `vnp_TxnRef` | Merchant reference duy nhất |
| `vnp_OrderInfo` | Mô tả đơn hàng |
| `vnp_OrderType` | `other` |
| `vnp_Locale` | `vn` hoặc `en` |
| `vnp_ReturnUrl` | Return URL đã cấu hình |
| `vnp_IpAddr` | IP client, ưu tiên giá trị đầu của `X-Forwarded-For` |
| `vnp_CreateDate` | Thời điểm tạo |
| `vnp_ExpireDate` | Thời điểm tạo cộng 15 phút |
| `vnp_SecureHash` | Chữ ký HMAC-SHA512 |

### 6.4 Xử lý Return URL

Controller chuyển toàn bộ query parameter cho `processReturn`.

Chữ ký không hợp lệ trả:

- `validSignature: false`
- `successful: false`
- Không cập nhật database

Nếu chữ ký hợp lệ, service tìm payment, tìm order, so sánh amount và yêu cầu đồng thời:

```text
vnp_ResponseCode == 00
vnp_TransactionStatus == 00
```

Chỉ khi cả hai bằng `00` giao dịch mới thành công.

### 6.5 Xử lý IPN

| `RspCode` | Ý nghĩa |
| --- | --- |
| `00` | Đã nhận và xử lý thông báo. |
| `01` | Không tìm thấy transaction phía merchant. |
| `02` | Payment đã được xác nhận. |
| `04` | Amount không khớp dữ liệu đã lưu. |
| `97` | Checksum không hợp lệ. |

Kiểm tra `PAID` giúp IPN có tính idempotent.

### 6.6 Chuyển trạng thái

Thành công:

```text
Payment: UNPAID -> PAID
Order:   PENDING -> CONFIRMED
```

Kết quả thất bại nhưng có chữ ký hợp lệ:

```text
Payment: UNPAID -> FAILED
Order:   giữ nguyên
```

Order đã ở trạng thái sau `PENDING` không bị chuyển lùi về `CONFIRMED`.

## 7. Thiết lập VNPay sandbox

Đăng ký tại:

```text
https://sandbox.vnpayment.vn/devreg/
```

Cần hai giá trị:

- `VNPAY_TMN_CODE`
- `VNPAY_HASH_SECRET`

Ngoài Return URL được gửi trong payment request, cần đăng ký IPN URL công khai trong cấu hình merchant.

Nếu tunnel HTTPS công khai là `https://example-tunnel.test`:

```text
Return URL: https://example-tunnel.test/api/v1/payments/vnpay/return
IPN URL:    https://example-tunnel.test/api/v1/payments/vnpay/ipn
```

VNPay không thể gọi `localhost` trên máy cá nhân. Localhost chỉ phù hợp cho browser return trên cùng máy hoặc mô phỏng callback thủ công.

## 8. Chạy ứng dụng với VNPay

```bash
docker start cnlt-hd-t7-postgres
```

```bash
DB_LOCAL=ecommerce \
DB_USERNAME_LOCAL=ecommerce \
DB_PASSWORD_LOCAL=ecommerce_local \
JWT_SECRET_KEY=0123456789abcdef0123456789abcdef0123456789abcdef \
VNPAY_TMN_CODE='your-sandbox-tmn-code' \
VNPAY_HASH_SECRET='your-sandbox-hash-secret' \
VNPAY_RETURN_URL='http://localhost:8080/api/v1/payments/vnpay/return' \
mvn spring-boot:run
```

Không commit secret sandbox hoặc production vào Git.

## 9. Kiểm thử sandbox đầy đủ

### 9.1 Tạo order chưa có payment

Ba order trong seed đã có payment nên endpoint VNPay sẽ từ chối chúng. Phải tạo order mới:

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

### 9.2 Tạo payment VNPay

```bash
PAYMENT_RESPONSE=$(
  curl --silent --request POST \
    --url http://localhost:8080/api/v1/payments/vnpay/create \
    --header 'Content-Type: application/json' \
    --data "{\"orderId\":${ORDER_ID},\"locale\":\"vn\"}"
)

echo "$PAYMENT_RESPONSE" | jq
```

Response mẫu:

```json
{
  "orderId": 4,
  "paymentId": 4,
  "transactionRef": "41793415700123",
  "status": "UNPAID",
  "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?..."
}
```

### 9.3 Mở URL

macOS:

```bash
PAYMENT_URL=$(echo "$PAYMENT_RESPONSE" | jq --raw-output '.paymentUrl')
open "$PAYMENT_URL"
```

Linux:

```bash
PAYMENT_URL=$(echo "$PAYMENT_RESPONSE" | jq --raw-output '.paymentUrl')
xdg-open "$PAYMENT_URL"
```

Thực hiện hoặc hủy giao dịch theo hướng dẫn tài khoản sandbox. VNPay sẽ redirect về Return URL.

### 9.4 Kiểm tra order

```bash
curl --silent \
  "http://localhost:8080/api/v1/orders/${ORDER_ID}" | jq
```

Sau callback thành công có chữ ký hợp lệ, status của order là `CONFIRMED`.

## 10. Mô phỏng callback hoàn toàn bằng terminal

Không cần gọi VNPay. Export credential development trước khi chạy server:

```bash
export VNPAY_TMN_CODE='LOCALTEST'
export VNPAY_HASH_SECRET='local-development-secret'

DB_LOCAL=ecommerce \
DB_USERNAME_LOCAL=ecommerce \
DB_PASSWORD_LOCAL=ecommerce_local \
JWT_SECRET_KEY=0123456789abcdef0123456789abcdef0123456789abcdef \
mvn spring-boot:run
```

Tạo order và payment bằng các lệnh ở trên, sau đó:

```bash
TXN_REF=$(echo "$PAYMENT_RESPONSE" | jq --raw-output '.transactionRef')
ORDER_TOTAL=$(curl --silent "http://localhost:8080/api/v1/orders/${ORDER_ID}" | jq --raw-output '.totalAmount')
VNP_AMOUNT=$((ORDER_TOTAL * 100))
```

Tạo chuỗi đúng thứ tự mà signer sử dụng:

```bash
HASH_DATA="vnp_Amount=${VNP_AMOUNT}&vnp_ResponseCode=00&vnp_TransactionNo=999999&vnp_TransactionStatus=00&vnp_TxnRef=${TXN_REF}"

SECURE_HASH=$(
  printf %s "$HASH_DATA" \
    | openssl dgst -sha512 -hmac "$VNPAY_HASH_SECRET" \
    | awk '{print $2}'
)
```

Gửi IPN thành công giả lập:

```bash
curl --get \
  --url http://localhost:8080/api/v1/payments/vnpay/ipn \
  --data-urlencode "vnp_Amount=${VNP_AMOUNT}" \
  --data-urlencode 'vnp_ResponseCode=00' \
  --data-urlencode 'vnp_TransactionNo=999999' \
  --data-urlencode 'vnp_TransactionStatus=00' \
  --data-urlencode "vnp_TxnRef=${TXN_REF}" \
  --data-urlencode "vnp_SecureHash=${SECURE_HASH}"
```

Kết quả:

```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

Gọi lại cùng lệnh để kiểm tra idempotency:

```json
{
  "RspCode": "02",
  "Message": "Order already Confirmed"
}
```

### Kiểm tra checksum sai

```bash
curl --get \
  --url http://localhost:8080/api/v1/payments/vnpay/ipn \
  --data-urlencode "vnp_Amount=${VNP_AMOUNT}" \
  --data-urlencode 'vnp_ResponseCode=00' \
  --data-urlencode 'vnp_TransactionStatus=00' \
  --data-urlencode "vnp_TxnRef=${TXN_REF}" \
  --data-urlencode 'vnp_SecureHash=invalid'
```

```json
{
  "RspCode": "97",
  "Message": "Invalid Checksum"
}
```

Database phải giữ nguyên khi checksum sai.

## 11. Các lỗi thường gặp

Chưa cấu hình credential, HTTP `400`:

```json
{
  "error": "Bad Request",
  "message": "Chưa cấu hình VNPAY_TMN_CODE và VNPAY_HASH_SECRET"
}
```

Order đã có payment, HTTP `400`:

```json
{
  "error": "Bad Request",
  "message": "Đơn hàng đã có phương thức thanh toán"
}
```

Locale không hỗ trợ, HTTP `400`:

```json
{
  "error": "Bad Request",
  "message": "Ngôn ngữ VNPay chỉ hỗ trợ vn hoặc en"
}
```

Không tìm thấy order, HTTP `404`:

```json
{
  "error": "Not Found",
  "message": "Không tìm thấy đơn hàng"
}
```

## 12. Kiểm thử tự động

```bash
DB_LOCAL=ecommerce \
DB_USERNAME_LOCAL=ecommerce \
DB_PASSWORD_LOCAL=ecommerce_local \
JWT_SECRET_KEY=0123456789abcdef0123456789abcdef0123456789abcdef \
mvn test
```

Chỉ chạy test VNPay:

```bash
mvn -Dtest=VnPayControllerTest,VnPayServiceTest,VnPaySignerTest test
```

Signer test dùng giá trị HMAC-SHA512 cố định, do đó kiểm tra output thực tế thay vì chỉ so sánh utility với chính nó.

## 13. Lưu ý trước khi dùng production

- Chỉ dùng Return và IPN URL HTTPS công khai.
- Quản lý secret bằng hạ tầng triển khai.
- Đặt unique constraint cho merchant transaction reference.
- Dùng row locking hoặc optimistic locking khi cập nhật payment.
- Có lịch sử payment attempt nếu cho phép thanh toán lại.
- Bổ sung đối soát transaction query với VNPay.
- Bổ sung Refund API.
- Audit log có cấu trúc nhưng không ghi secret.
- Theo dõi và cảnh báo IPN bị từ chối.
- Bổ sung authentication và kiểm tra quyền sở hữu order.

Tài liệu chính thức:

- VNPay sandbox: `https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html`
- Đăng ký sandbox: `https://sandbox.vnpayment.vn/devreg/`
- Bảng mã lỗi: `https://sandbox.vnpayment.vn/apis/docs/bang-ma-loi/`
