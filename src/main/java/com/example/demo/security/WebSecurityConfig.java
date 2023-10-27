package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.service.ICustomerService;
import com.example.demo.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final ICustomerService customerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public WebSecurityConfig(ICustomerService customerService, BCryptPasswordEncoder bCryptPasswordEncoder,
                             ApplicationProperties applicationProperties) {
        this.customerService = customerService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.applicationProperties = applicationProperties;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customerService)
                .passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(customerService, authenticationManager, applicationProperties);
        authenticationFilter.setFilterProcessesUrl(applicationProperties.getLoginEndpoint());
        return http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers(new AntPathRequestMatcher(Constant.ACTUATOR_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.H2_CONSOLE_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.CUSTOMER_URL)).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(new AuthorizationFilter(authenticationManager, applicationProperties))
                .addFilter(authenticationFilter)
                .authenticationManager(authenticationManager)
                .csrf().disable()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher(Constant.H2_CONSOLE_URL));
    }
}

