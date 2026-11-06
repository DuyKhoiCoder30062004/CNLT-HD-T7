# Giai đoạn 1: Build file JAR bằng bản phân phối Maven chính thức chạy trên nền Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy tệp pom.xml và toàn bộ mã nguồn vào trong container
COPY pom.xml .
COPY src ./src

# Tiến hành biên dịch dự án ra file JAR và bỏ qua công đoạn chạy test để rút ngắn thời gian build
RUN mvn clean package -DskipTests

# Giai đoạn 2: Tạo Image môi trường gọn nhẹ (chỉ chứa JRE 17) để chạy ứng dụng
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Khai báo cổng 8080 mà Spring Boot thường dùng
EXPOSE 8080

# Sao chép file JAR đã hoàn thiện từ giai đoạn build sang môi trường runtime
COPY --from=build /app/target/*.jar app.jar

# Cấu hình lệnh khởi chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]
