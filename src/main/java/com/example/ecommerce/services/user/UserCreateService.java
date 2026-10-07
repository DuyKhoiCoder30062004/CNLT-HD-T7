package com.example.ecommerce.services.user;

import com.example.ecommerce.entities.User;
import com.example.ecommerce.interfaces.user.IUserCreate;
import com.example.ecommerce.repositories.user.UserRepository;
import com.example.ecommerce.dtos.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class UserCreateService implements IUserCreate {

    private final UserRepository userRepository;

    public UserCreateService(
        UserRepository userRepository
    ){
        this.userRepository = userRepository;
    }

    @Override
    public User create(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email đã tồn tại, hãy dùng email khác");
        }

        if(userRepository.existsByPhone(request.getPhone())){
            throw new RuntimeException("Số điện thoại đã tồn tại, hãy dùng số khác");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại, hãy dùng tên khác");
        }

        String hashedPassword = this.hashPassword(request.getPassword());

        User user = new User(
            request.getUsername(),
            request.getEmail(),
            request.getPhone(),
            hashedPassword
        );
        return userRepository.save(user);
    }

}