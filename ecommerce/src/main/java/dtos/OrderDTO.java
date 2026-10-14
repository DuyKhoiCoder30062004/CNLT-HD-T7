package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO{

    private Integer id;

    private Integer userId;

    private String username;

    private String status;

    private Integer totalAmount;

    private ReceiverDTO receiver;

    private List<OrderDetailDTO> products;

    private LocalDateTime createdAt;
}
