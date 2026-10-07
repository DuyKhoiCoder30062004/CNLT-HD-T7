package com.example.ecommerce.services.user;

import com.example.ecommerce.entities.User;
import com.example.ecommerce.interfaces.user.IUserQuery;
import com.example.ecommerce.repositories.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserQueryService implements IUserQuery {

    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone){
        return userRepository.existsByPhone(phone);
    }

    @Override
    public User findByPhone(String phone){
        User user= userRepository.findByPhone(phone);
        if(user == null)
		    throw new RuntimeException("Không tìm thấy người dùng");
		return user;
    }
}
