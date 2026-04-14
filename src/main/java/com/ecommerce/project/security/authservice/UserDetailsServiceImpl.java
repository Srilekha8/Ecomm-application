package com.ecommerce.project.security.authservice;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("user not found with username: "+username));
        //to covert User object into UserDetail object
        return UserDetailsImpl.build(user);
    }
}
