package com.example.ecommerce.enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
	USER("Khách hàng"),
	MOD("Nhân viên"),
	ADMIN("Quản trị viên");

	private final String description;
    @JsonCreator
    public static Role fromValue(int value) {
        return switch (value) {
            case 0 -> USER;
            case 1 -> MOD;
            case 2 -> ADMIN;
            default -> throw new IllegalArgumentException("Không tồn tại: " + value);
        };
    }


	private Role(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}
