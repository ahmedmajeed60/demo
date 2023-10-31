package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.dto.CustomerDto;
import com.example.demo.service.ICustomerService;
import com.example.demo.util.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.List;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    private final ApplicationProperties applicationProperties;
    private final TokenUtil tokenUtil;
    private final ICustomerService customerService;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               ApplicationProperties applicationProperties,
                               TokenUtil tokenUtil,
                               ICustomerService customerService) {
        super(authenticationManager);
        this.applicationProperties = applicationProperties;
        this.tokenUtil = tokenUtil;
        this.customerService = customerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String authorizationHeader = req.getHeader(applicationProperties.getAuthorizationHeader());
        if (authorizationHeader == null || !authorizationHeader.startsWith(Constant.BEARER)) {
            LOGGER.debug("[{}] header is not present.", applicationProperties.getAuthorizationHeader());
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
        String token = authorizationHeader.replace(Constant.BEARER, "");
        String customerId = tokenUtil.getSubjectFromToken(token);
        if (customerId == null) {
            throw new UsernameNotFoundException("Authorization is failed due to invalid token " + token);
        }
        LOGGER.debug("Authorization is successful for customerId [{}].", customerId);
        CustomerDto customerDto = customerService.getCustomerByCustomerId(customerId);
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(
                Constant.ROLE + customerDto.getRole());
        return new UsernamePasswordAuthenticationToken(customerId, null, List.of(simpleGrantedAuthority));
    }


}
