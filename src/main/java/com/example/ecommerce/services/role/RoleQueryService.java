package com.example.ecommerce.services.role;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.entities.Role;
import com.example.ecommerce.interfaces.role.IRoleQuery;
import com.example.ecommerce.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleQueryService implements IRoleQuery {

    private final RoleRepository roleRepository;

    public RoleQueryService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean existsByName(String name){
        return roleRepository.existsByName(name);
    }

    @Override
    public Role findByUser(){
        Role role= roleRepository.findByUser();
        if(role == null)
		    throw new ResourceNotFoundException("Không tìm thấy role");
		return role;
    }
}
