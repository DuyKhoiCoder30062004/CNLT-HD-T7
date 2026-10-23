package com.example.ecommerce.config;

import com.example.ecommerce.entities.Role;
import com.example.ecommerce.entities.Category;
import com.example.ecommerce.entities.PaymentStatus;
import com.example.ecommerce.entities.OrderStatus;
import com.example.ecommerce.repositories.RoleRepository;
import com.example.ecommerce.repositories.PaymentStatusRepository;
import com.example.ecommerce.repositories.OrderStatusRepository;
import com.example.ecommerce.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            String[][] roles = {
                {"ADMIN", "Quản tị viên hệ thống"},
                {"USER", "Người dùng thông thường"},
            };

            for (String[] roleData : roles) {
                String name = roleData[0];
                String description = roleData[1];

                if (!roleRepository.existsByName(name)) {
                    roleRepository.save(
                        new Role(name, description)
                    );
                }
            }
        };
    }

    @Bean
	CommandLineRunner initCategories(CategoryRepository categoryRepository) {
    return args -> {
        String[][] datas = {
                {"WATCH", "Đồng hồ thông minh"},
                {"PHONE", "Điện thoại thông minh"},
                {"LAPTOP", "Máy tính"}
            };

            for (String[] data : datas) {
                String name = data[0];
                String description = data[1];

                if (!categoryRepository.existsByName(name)) {
                    categoryRepository.save(
                        new Category(name, description)
                    );
                }
            }
	    };
	}

    @Bean
	CommandLineRunner initOrderStatuses(OrderStatusRepository orderStatusRepository) {
    return args -> {
        String[][] datas = {
                {"PENDING", "Đang đợi xử lý"},
                {"PACKING", "Đang đóng gói hàng"},
                {"SHIPPING", "Đang vận chuyển hàng"},
                {"SUCCESSFULLY", "Đơn hàng thành công"},
                {"FAILED", "Đơn hàng thất bại"},
            };

            for (String[] data : datas) {
                String name = data[0];
                String description = data[1];

                if (!orderStatusRepository.existsByName(name)) {
                    orderStatusRepository.save(
                        new OrderStatus(name, description)
                    );
                }
            }
	    };
	}

    @Bean
	CommandLineRunner initPaymentStatuses(PaymentStatusRepository paymentStatusRepository) {
    return args -> {
        String[][] datas = {
                {"SUCCESSFULLY", "Thanh toán thành công"},
                {"FAILED", "Thanh toán đơn thất bại"},
            };

            for (String[] data : datas) {
                String name = data[0];
                String description = data[1];

                if (!paymentStatusRepository.existsByName(name)) {
                    paymentStatusRepository.save(
                        new PaymentStatus(name, description)
                    );
                }
            }
	    };
	}
}