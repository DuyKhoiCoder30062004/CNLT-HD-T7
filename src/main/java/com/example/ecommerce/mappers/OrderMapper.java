
package com.example.ecommerce.mappers;

import com.example.ecommerce.dtos.order.OrderDTO;
import com.example.ecommerce.dtos.order.OrderDetailDTO;
import com.example.ecommerce.dtos.receiver.ReceiverDTO;
import com.example.ecommerce.entities.Image;
import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.OrderDetail;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.Receiver;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDTO toDTO(Order order, List<OrderDetail> details) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setStatus(order.getStatus().getName());
        dto.setTotalAmount(order.getTotalPrice().intValue());
        dto.setReceiver(toReceiverDTO(order.getReceiver()));
        dto.setProducts(details.stream().map(OrderMapper::toDetailDTO).toList());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    private static ReceiverDTO toReceiverDTO(Receiver receiver) {
        ReceiverDTO dto = new ReceiverDTO();
        dto.setId(receiver.getId());
        dto.setName(receiver.getFullname());
        dto.setPhone(receiver.getPhone());
        dto.setWardName(receiver.getWard().getName());
        dto.setProvinceName(receiver.getWard().getProvince().getName());
        return dto;
    }

    private static OrderDetailDTO toDetailDTO(OrderDetail detail) {
        Product product = detail.getProduct();
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(detail.getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setImagePath(getImagePath(product));
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getPrice().intValue());
        dto.setSubtotal(detail.getTotalPrice().intValue());
        return dto;
    }

    private static String getImagePath(Product product) {
        if (product.getThumbnail() != null && !product.getThumbnail().isBlank()) {
            return product.getThumbnail();
        }
        return product.getImages().stream()
                .findFirst()
                .map(Image::getPath)
                .orElse(null);
    }
}
