package com.example.demo.util;

public class Constant {

    private Constant() {
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    public static final String ACTUATOR_URL = "/actuator/**";
    public static final String H2_CONSOLE_URL = "/h2-console/**";
    public static final String OPEN_API_URL = "/api-docs/**";
    public static final String SWAGGER_URL = "/swagger-ui/**";
    public static final String LOGIN_URL = "/api/login";

    public static final String ROLE = "ROLE_";
    public static final String HAS_ADMIN_ROLE = "hasRole('ADMIN')";
    public static final String HAS_CLIENT_ROLE = "hasRole('CLIENT')";
    public static final String HAS_ADMIN_OR_CLIENT_ROLE = "hasAnyRole('ADMIN', 'CLIENT')";
}
