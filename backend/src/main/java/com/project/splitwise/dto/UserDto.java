package com.project.splitwise.dto;

import com.project.splitwise.entity.Role;
import com.project.splitwise.entity.User;
import com.project.splitwise.entity.UserGroup;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private List<String> groups = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public UserDto(User inputUser){
        this.id = inputUser.getId();
        this.userName = inputUser.getUserName();
        this.email = inputUser.getEmail();
        this.password = inputUser.getPassword();
        this.phoneNumber = inputUser.getPhoneNumber();

        for(UserGroup i : inputUser.getGroups())groups.add(i.getGroup().getGroupName());
        for(Role i : inputUser.getRoles()) roles.add(i.getName());

    }
}
