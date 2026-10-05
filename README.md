# Feature: Authentication

This feature provides user authentication and registration.

## Login

Authenticate a user using their phone number and password.

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

## Register

Create a new user account.

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

## Get Current User

Get information about the currently authenticated user.

**Endpoint**

```http
GET /api/auth/me
```

**Headers**

```http
Authorization: Bearer <your-jwt-token>
```

Example:

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
## Dependencies Required

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