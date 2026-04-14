package com.ecommerce.project.security;

import com.ecommerce.project.security.authservice.UserDetailsImpl;
import com.ecommerce.project.security.authservice.UserDetailsServiceImpl;
import com.ecommerce.project.security.jwt.AuthEntryPoint;
import com.ecommerce.project.security.jwt.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Bean
    public AuthenticationFilter authJwtFilter() {
        return new AuthenticationFilter();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3-api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                //.requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated());
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));// to make frames load or else, it's all empty
        http.csrf(csrf -> csrf.disable());
        http.exceptionHandling(exception->
                exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authJwtFilter(), UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //In order to completly avoid spring security
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2-api-docs/**",
                "/webjars/**",
                "/configuration/**",
                "/swagger-resources/**",
                "/swagger-ui.html"));
    }
}
