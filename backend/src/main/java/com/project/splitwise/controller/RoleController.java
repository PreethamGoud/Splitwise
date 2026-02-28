package com.project.splitwise.controller;

import com.project.splitwise.entity.Role;
import com.project.splitwise.entity.User;
import com.project.splitwise.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<?> saveRole(@RequestBody Role inputRole){
        try {
            Role savedRole = roleService.saveRole(inputRole);

            return new ResponseEntity<>(savedRole , HttpStatus.CREATED);
        }
        catch (Exception e){
            log.error("error in creating user",e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }
}
