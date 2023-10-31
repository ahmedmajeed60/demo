package com.example.demo.util;

public class Constant {

    private Constant() {
    }

    public static final String BEARER = "Bearer ";
    public static final String ACTUATOR_URL = "/actuator/**";
    public static final String H2_CONSOLE_URL = "/h2-console/**";
    public static final String ROLE = "ROLE_";
    public static final String HAS_ADMIN_ROLE = "hasRole('ADMIN')";
    public static final String HAS_CLIENT_ROLE = "hasRole('CLIENT')";
}
