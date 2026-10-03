package com.example.ecommerce.enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
	WOOD("Đồ gỗ"),
	METAL("Đồ kim loại"),
	;

	private final String description;
    @JsonCreator
    public static Category fromValue(int value) {
        return switch (value) {
            case 0 -> WOOD;
            case 1 -> METAL;
            default -> throw new IllegalArgumentException("Không tồn tại: " + value);
        };
    }


	private Category(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
