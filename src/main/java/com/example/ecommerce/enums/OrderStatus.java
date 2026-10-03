package com.example.ecommerce.enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderStatus {
	PENDING("Đang đợi"),
	PACKING("Đang đóng gói"),
	SHIPPING("Đang vận chuyển"),
	SUCCESSFUL("Thành công"),
	CANCELD("Đã hủy"),
	;

	private final String description;
	
    @JsonCreator
    public static OrderStatus fromValue(int value) {
        return switch (value) {
            case 0 -> PENDING;
            case 1 -> PACKING;
            case 2 -> SHIPPING;
            case 3 -> SUCCESSFUL;
            case 4 -> CANCELD;
            default -> throw new IllegalArgumentException("Không tồn tại: " + value);
        };
    }


	private OrderStatus(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
