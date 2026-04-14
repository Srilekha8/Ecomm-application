package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.authpayload.LoginRequest;
import com.ecommerce.project.security.authpayload.LoginResponse;
import com.ecommerce.project.security.authpayload.SignUpRequest;
import com.ecommerce.project.security.authpayload.SignUpResponse;
import com.ecommerce.project.security.authservice.UserDetailsImpl;
import com.ecommerce.project.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateuser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", 401);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(role->role.getAuthority()).toList();
        LoginResponse loginResp = new LoginResponse(userDetails.getId(),userDetails.getUsername(), roles);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(loginResp);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody SignUpRequest signupReq){
        if(userRepository.existsByUserName(signupReq.getUsername()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body( new SignUpResponse("Username is already taken!"));

        if(userRepository.existsByEmail(signupReq.getEmail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body( new SignUpResponse("Email is already existed!"));

        Set<String> strRoles = signupReq.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles==null || strRoles.isEmpty()){
            Role userRole = roleRepository.findByRoleName((AppRole.ROLE_USER))
                    .orElseThrow(()->new RuntimeException("Role not found"));
            roles.add(userRole);
        }else{
            strRoles.stream().forEach(role->{
                switch (role.toLowerCase()){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Role not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(()->new RuntimeException("Role not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName((AppRole.ROLE_USER))
                                .orElseThrow(()->new RuntimeException("Role not found"));
                        roles.add(userRole);
                }

            });
        }
        User user = new User(
                signupReq.getUsername(),signupReq.getEmail(),
                passwordEncoder.encode(signupReq.getPassword()));
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new SignUpResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication){
        System.out.println("Current username is: " + authentication.getPrincipal());
        if(authentication!=null){
            return authentication.getName();
        }
        else {
            return "Current User";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(role->role.getAuthority()).toList();
        LoginResponse loginResp = new LoginResponse(userDetails.getId(),userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(loginResp);
    }

    @GetMapping("/signout")
    public ResponseEntity<?> signout(){
        ResponseCookie emptyCookie = jwtUtils.replaceJwtcookieWithCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, emptyCookie.toString())
                .body("Signout successful");

    }
}
