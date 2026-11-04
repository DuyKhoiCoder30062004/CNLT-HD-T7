package com.example.ecommerce.dtos.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}
