package com.example.ecommerce.services.order;

import com.example.ecommerce.dtos.order.OrderDTO;
import com.example.ecommerce.dtos.order.OrderDetailDTO;
import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.OrderDetail;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.Receiver;
import com.example.ecommerce.entities.Status;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.interfaces.order.IOrder;
import com.example.ecommerce.mappers.OrderMapper;
import com.example.ecommerce.repositories.OrderDetailRepository;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.repositories.ReceiverRepository;
import com.example.ecommerce.repositories.StatusRepository;
import com.example.ecommerce.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrder {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ReceiverRepository receiverRepository;
    private final ProductRepository productRepository;
    private final StatusRepository statusRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository,
            ReceiverRepository receiverRepository,
            ProductRepository productRepository,
            StatusRepository statusRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.receiverRepository = receiverRepository;
        this.productRepository = productRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Integer id) {
        return toDTO(findOrder(id));
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (orderDTO.getReceiver().getId() == null) {
            throw new IllegalArgumentException("Người nhận phải có id");
        }
        Receiver receiver = receiverRepository.findById(orderDTO.getReceiver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận"));

        Status status = findStatus(orderDTO.getStatus() == null ? "PENDING" : orderDTO.getStatus());
        List<OrderDetail> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setReceiver(receiver);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());

        for (OrderDetailDTO detailDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm " + detailDTO.getProductId()
                    ));
            validateInventory(product, detailDTO.getQuantity());

            BigDecimal unitPrice = BigDecimal.valueOf(
                    product.getTotalPrice() == null ? product.getPrice() : product.getTotalPrice()
            );
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(detailDTO.getQuantity()));
            details.add(new OrderDetail(product, order, unitPrice, detailDTO.getQuantity(), subtotal));
            total = total.add(subtotal);

            product.setInventoryQuantity(product.getInventoryQuantity() - detailDTO.getQuantity());
            product.setSoldQuantity(product.getSoldQuantity() + detailDTO.getQuantity());
        }

        order.setPrice(total);
        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);
        List<OrderDetail> savedDetails = orderDetailRepository.saveAll(details);
        return OrderMapper.toDTO(savedOrder, savedDetails);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Integer id, String statusName) {
        Order order = findOrder(id);
        order.setStatus(findStatus(statusName));
        order.setEditedAt(LocalDateTime.now());
        return toDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        Order order = findOrder(id);
        order.setDeletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private OrderDTO toDTO(Order order) {
        return OrderMapper.toDTO(
                order,
                orderDetailRepository.findAllByOrderIdOrderById(order.getId())
        );
    }

    private Order findOrder(Integer id) {
        return orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }

    private Status findStatus(String statusName) {
        return statusRepository.findByNameIgnoreCase(statusName)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy trạng thái đơn hàng"));
    }

    private void validateInventory(Product product, Integer quantity) {
        if (product.getInventoryQuantity() == null || product.getInventoryQuantity() < quantity) {
            throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ tồn kho");
        }
    }
}
