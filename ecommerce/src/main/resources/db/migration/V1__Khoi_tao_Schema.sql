CREATE TABLE Role (
                      id SERIAL PRIMARY KEY,
                      name NVARCHAR(255),
                      description TEXT
);

CREATE TABLE "User" (
                        id SERIAL PRIMARY KEY,
                        username NVARCHAR(255),
                        phone VARCHAR(20),
                        email VARCHAR(255),
                        password VARCHAR(255),
                        avatar VARCHAR(255),
                        roleId INT REFERENCES Role(id),
                        createdAt TIMESTAMP,
                        deletedAt TIMESTAMP,
                        editedAt TIMESTAMP
);

CREATE TABLE Province (
                          id SERIAL PRIMARY KEY,
                          name NVARCHAR(255)
);

CREATE TABLE District (
                          id SERIAL PRIMARY KEY,
                          name NVARCHAR(255),
                          provinceId INT REFERENCES Province(id)
);

CREATE TABLE Ward (
                      id SERIAL PRIMARY KEY,
                      name NVARCHAR(255),
                      districtId INT REFERENCES District(id)
);

CREATE TABLE Receiver (
                          id SERIAL PRIMARY KEY,
                          fullname NVARCHAR(255),
                          phone VARCHAR(20),
                          wardId INT REFERENCES Ward(id),
                          createdAt TIMESTAMP,
                          deletedAt TIMESTAMP,
                          editedAt TIMESTAMP
);

CREATE TABLE Status (
                        id SERIAL PRIMARY KEY,
                        name NVARCHAR(255),
                        description TEXT
);

CREATE TABLE "Order" (
                         id SERIAL PRIMARY KEY,
                         receiverId INT REFERENCES Receiver(id),
                         userId INT REFERENCES "User"(id),
                         statusId INT REFERENCES Status(id),
                         createdAt TIMESTAMP,
                         deletedAt TIMESTAMP,
                         editedAt TIMESTAMP
);

CREATE TABLE Category (
                          id SERIAL PRIMARY KEY,
                          name NVARCHAR(255),
                          description TEXT
);

CREATE TABLE Product (
                         id SERIAL PRIMARY KEY,
                         name NVARCHAR(255),
                         price INT,
                         thumbnail VARCHAR(255),
                         weight INT,
                         description TEXT,
                         discount INT,
                         categoryId INT REFERENCES Category(id),
                         inventoryQuantity INT,
                         soldQuantity INT,
                         createdAt TIMESTAMP,
                         deletedAt TIMESTAMP,
                         editedAt TIMESTAMP
);

CREATE TABLE Image (
                       id SERIAL PRIMARY KEY,
                       productId INT REFERENCES Product(id),
                       path VARCHAR(255),
                       alt NVARCHAR(255)
);

CREATE TABLE OrderDetail (
                             id SERIAL PRIMARY KEY,
                             productId INT REFERENCES Product(id),
                             orderId INT REFERENCES "Order"(id),
                             quantity INT
);

CREATE TABLE Payment (
                         id SERIAL PRIMARY KEY,
                         orderId INT REFERENCES "Order"(id),
                         statusId INT REFERENCES Status(id),
                         method NVARCHAR(50),
                         amount INT,
                         status NVARCHAR(50),
                         transactionId VARCHAR(255),
                         createdAt TIMESTAMP,
                         deletedAt TIMESTAMP,
                         editedAt TIMESTAMP
);
