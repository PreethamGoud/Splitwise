package com.project.splitwise.controller;

import com.project.splitwise.dto.UserDto;
import com.project.splitwise.entity.User;
import com.project.splitwise.service.UserDetailsServiceImplementation;
import com.project.splitwise.service.UserService;
import com.project.splitwise.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/public")
public class PublicController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUrl;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImplementation userDetailsServiceImplementaion;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/signup")
    public ResponseEntity<?> saveUser(@RequestBody User inputUser){
        try {
            if(inputUser.getUserName().length() == 0 || inputUser.getEmail().length() == 0){
                throw new IllegalArgumentException("userName or Email can't be blank");
            }
            User savedUser = userService.saveUser(inputUser);

            UserDto userDto = new UserDto(savedUser);

            return new ResponseEntity<>(userDto , HttpStatus.CREATED);
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

    @PostMapping("/login")  //user creation public rehna chahiye
    public ResponseEntity<String> login(@RequestBody User newUser){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(newUser.getUserName() , newUser.getPassword()));
            UserDetails userDetails = userDetailsServiceImplementaion.loadUserByUsername(newUser.getUserName());
            //generate token
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt , HttpStatus.OK);
        }
        catch (Exception e){
            log.error("exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect userName or password",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/oauth/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {
            //exchange authcode for tokens
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id",clientId);
            params.add("client_secret",clientSecret);
            params.add("redirect_uri",redirectUrl);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            //take token out from token response
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");

            //hit google api to get info using the token
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = null;
                try{
                    userDetails = userDetailsService.loadUserByUsername(email);
                }catch (Exception e){
                    User user = new User();
                    user.setEmail(email);
                    user.setUserName(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));  //generate random password
//                    user.setRoles(Arrays.asList("USER"));
                    User savedUser = userService.saveUser(user);
//                    userRepository.save(user);
                }
                String jwtToken = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Exception occurred while handleGoogleCallback ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/healthCheck")
    public ResponseEntity<?> fetchHealth(){
        return new ResponseEntity<>("Splitwise health ok ✌.|•͡˘‿•͡˘|.✌" , HttpStatus.OK);
    }
}
