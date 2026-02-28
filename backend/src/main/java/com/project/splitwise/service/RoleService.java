package com.project.splitwise.service;

import com.project.splitwise.entity.Role;
import com.project.splitwise.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role saveRole(Role inputRole){
        return roleRepository.save(inputRole);
    }
}
