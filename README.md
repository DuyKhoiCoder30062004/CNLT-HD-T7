# Tính năng: Authentication (Xác thưc)

Tính năng này cung cấp xác thực người dùng và đăng ký mới.

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
Đây là phần khá quan trọng trong bảo mật để đảm bảo thông tin payload không bị thay đổi bởi người khác. Phần này lấy secret key từ tệp môi trường (env).

# Screenshot khởi động tính năng Auth
![Màn hình Termianl](./img/auth/cmd1.png)
![](./img/auth/cmd2.png)
![](./img/auth/cmd3.png)
