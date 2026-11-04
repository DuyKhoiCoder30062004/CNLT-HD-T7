package com.example.ecommerce.dtos.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VnPayCreateRequest {

    @NotNull(message = "Đơn hàng không được để trống")
    private Integer orderId;

    private String locale = "vn";
}
