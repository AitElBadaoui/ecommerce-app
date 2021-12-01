package com.example.ecommerce.security;

import com.example.ecommerce.model.persistence.User;
import com.example.ecommerce.util.LogMF;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
           User credentials = new ObjectMapper().readValue(req.getInputStream(),
                    User.class);
            logger.info(LogMF.format("attemptAuthentication", "Attempting Auth for user " + credentials.getUsername() + "."));
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        String token = com.example.ecommerce.security.JWTTokenFactory.createFor(username);
        res.addHeader(com.example.ecommerce.security.SecurityConstants.HEADER_STRING, com.example.ecommerce.security.SecurityConstants.TOKEN_PREFIX + token);
        logger.info(LogMF.format("successfulAuthentication", "Login/Token Creation succeeded for user " + username + "."));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        logger.info(LogMF.format("unsuccessfulAuthentication", "Incorrect login credentials.  Login failed.", failed));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
