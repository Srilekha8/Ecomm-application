package com.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class AuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);


    //create Autentication object and set it into securityy context
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("AuthenticationFilter running for: " + request.getRequestURI());
        try {
            String jwt = jwtUtils.getJwtFromCookie(request);
            System.out.println("jwt token from the request: " + jwt);
            if(jwt != null && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.usernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //Creating Authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                        (userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetails(request));
                //set Authentication object to context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("User roles : {}", userDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
            logger.error("Username not found");
        }
        catch (Exception e) {
            logger.error("cannot set user authentication");
        }
        filterChain.doFilter(request,response);
    }
}
