package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
public class V3__SeedVietnameseData extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        // 1. Tạo bảng tạm
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("CREATE TEMP TABLE staging_vietnam (ma TEXT, ten TEXT, cap TEXT, nghi_quyet TEXT, ma_tp TEXT, tinh_thanh_pho TEXT)");
        }

        String csvPath = "db/migration/data/Vietnamese-Administrative-Units-Dataset.txt";
        String insertStagingSql = "INSERT INTO staging_vietnam (ma, ten, cap, nghi_quyet, ma_tp, tinh_thanh_pho) VALUES (?, ?, ?, ?, ?, ?)";

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(csvPath);
        if (is == null) {
            throw new RuntimeException("KHÔNG TÌM THẤY FILE CSV TẠI: " + csvPath);
        }

        int rawLinesRead = 0;
        int rowsInsertedToStaging = 0;

        try (var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             PreparedStatement pstmt = context.getConnection().prepareStatement(insertStagingSql)) {
            
            String line;
            boolean isHeader = true;
            String separator = ";"; 
            while ((line = reader.readLine()) != null) {
                rawLinesRead++;
                
                // Loại bỏ ký tự BOM ẩn nếu có ở đầu file
                if (rawLinesRead == 1) {
                    line = line.replace("\uFEFF", "");
                    // Tự động nhận diện dấu phân tách dựa vào dòng đầu tiên
                    if (!line.contains(";") && line.contains(",")) {
                        separator = ",";
                    }
                    System.out.println("[INFO] Flyway Seed: Dòng đầu tiên đọc được: " + line);
                    System.out.println("[INFO] Flyway Seed: Sử dụng dấu phân tách: '" + separator + "'");
                }

                if (isHeader) { 
                    isHeader = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(separator, -1); 
                if (data.length >= 6) {
                    for (int i = 0; i < 6; i++) {
                        String cleanValue = data[i].trim().replaceAll("^\"|\"$", "");
                        pstmt.setString(i + 1, cleanValue.isEmpty() ? null : cleanValue);
                    }
                    pstmt.addBatch();
                    rowsInsertedToStaging++;
                    
                    if (rowsInsertedToStaging % 1000 == 0) {
                        pstmt.executeBatch();
                    }
                } else {
                    // In ra log nếu có dòng bị lỗi cấu trúc cột để chẩn đoán
                    if (rowsInsertedToStaging < 5) {
                        System.out.println("[WARNING] Cảnh báo dòng lỗi cột (Kích thước mảng " + data.length + "): " + line);
                    }
                }
            }
            pstmt.executeBatch(); 
        }

        System.out.println("[SUCCESS] Flyway Seed: Tổng số dòng đọc từ file: " + rawLinesRead);
        System.out.println("[SUCCESS] Flyway Seed: Số dòng hợp lệ đã nạp vào bảng tạm: " + rowsInsertedToStaging);

        // 3. Đổ dữ liệu từ bảng tạm vào các bảng chính
        if (rowsInsertedToStaging > 0) {
            try (Statement stmt = context.getConnection().createStatement()) {
                // Nạp Tỉnh/Thành phố
                int provincesInserted = stmt.executeUpdate(
                    "INSERT INTO provinces (name) " +
                    "SELECT DISTINCT TRIM(BOTH FROM tinh_thanh_pho) " +
                    "FROM staging_vietnam s " +
                    "WHERE tinh_thanh_pho IS NOT NULL " +
                    "AND NOT EXISTS (SELECT 1 FROM provinces p WHERE p.name = TRIM(BOTH FROM s.tinh_thanh_pho))"
                );
                System.out.println("[INFO] Số bản ghi thêm vào bảng PROVINCES: " + provincesInserted);
                
                // Nạp Phường/Xã
                int wardsInserted = stmt.executeUpdate(
                    "INSERT INTO wards (name, province_id) " +
                    "SELECT DISTINCT TRIM(BOTH FROM s.ten), p.id " +
                    "FROM staging_vietnam s " +
                    "INNER JOIN provinces p ON TRIM(BOTH FROM p.name) = TRIM(BOTH FROM s.tinh_thanh_pho) " +
                    "WHERE s.ten IS NOT NULL"
                );
                System.out.println("[INFO] Số bản ghi thêm vào bảng WARDS: " + wardsInserted);
            }
        } else {
            System.out.println("[FAILED] Không có dữ liệu hợp lệ nào được đưa vào bảng tạm. Vui lòng kiểm tra lại định dạng file CSV.");
        }
    }
}
