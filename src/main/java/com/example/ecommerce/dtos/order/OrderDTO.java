package com.example.ecommerce.dtos.order;

import com.example.ecommerce.dtos.receiver.ReceiverDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Integer id;

    @NotNull(message = "Người dùng không được để trống")
    private Integer userId;

    private String username;

    private String status;

    private Integer totalAmount;

    @Valid
    @NotNull(message = "Người nhận không được để trống")
    private ReceiverDTO receiver;

    @NotEmpty(message = "Đơn hàng phải có ít nhất một sản phẩm")
    private List<@Valid OrderDetailDTO> products;

    private LocalDateTime createdAt;
}
