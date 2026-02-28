package com.project.splitwise.service;

//ye bnaya h spring security ke liye bs
//config -> SpringSecurity me use kia h ise

//How It Works in Spring Security
//  1.Authentication Process :
//  .When a user tries to log in, Spring Security calls the loadUserByUsername method with the provided username.
//  .This method retrieves the user details and converts them into a UserDetails object.

//  2.Password Verification
//  .Spring Security automatically compares the password provided in the login request with the one stored in the database (handled internally).

//  3.Role-based Authorization
//  .The roles provided in the UserDetails object are used by Spring Security to determine the user's access rights to various resources.

import com.project.splitwise.entity.User;
import com.project.splitwise.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection instead of @Autowired
    public UserDetailsServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert Set<Role> to array of role names (with "ROLE_" prefix)
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}

