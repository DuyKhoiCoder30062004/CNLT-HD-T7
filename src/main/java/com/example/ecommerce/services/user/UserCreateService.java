package com.example.ecommerce.services.user;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.entities.Role;
import com.example.ecommerce.interfaces.role.IRoleQuery;
import com.example.ecommerce.interfaces.user.IUserCreate;
import com.example.ecommerce.repositories.UserRepository;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class UserCreateService implements IUserCreate {

    private final UserRepository userRepository;
    private final IRoleQuery roleQueryService;

    public UserCreateService(
        UserRepository userRepository,
        IRoleQuery roleQueryService
    ){
        this.userRepository = userRepository;
        this.roleQueryService = roleQueryService;
    }

    @Override
    public User create(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceNotFoundException("Email đã tồn tại, hãy dùng email khác");
        }

        if(userRepository.existsByPhone(request.getPhone())){
            throw new ResourceNotFoundException("Số điện thoại đã tồn tại, hãy dùng số khác");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new ResourceNotFoundException("Tên đăng nhập đã tồn tại, hãy dùng tên khác");
        }

        String hashedPassword = this.hashPassword(request.getPassword());
        Role role = roleQueryService.findByUser();

        User user = new User(
            request.getUsername(),
            request.getPhone(),
            request.getEmail(),
            hashedPassword,
            role
        );
        return userRepository.save(user);
    }

}