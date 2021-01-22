package com.kahago.kahagoservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.controller.SignInController;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.SignInReq;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static com.kahago.kahagoservice.security.SecurityConstant.*;

/**
 * @author Hendro yuwono
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private UserServiceSecurity userServiceSecurity;
    private SignInController signInController;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserServiceSecurity userServiceSecurity, SignInController signInController) {
        this.authenticationManager = authenticationManager;
        this.userServiceSecurity = userServiceSecurity;
        this.signInController = signInController;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        SignInReq user;
        try {
            System.out.println(request.getInputStream());
            user = new ObjectMapper().readValue(request.getInputStream(), SignInReq.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        ZonedDateTime expired = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRED_TOKEN, ChronoUnit.DAYS);
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(Date.from(expired.toInstant()))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        LocalDateTime times = LocalDateTime.ofInstant(Instant.ofEpochMilli(expired.toInstant().toEpochMilli()), ZoneId.systemDefault());
        UserDetails userDetails = Optional.ofNullable(userServiceSecurity.loadUserByUsername(username)).orElseThrow(() -> new NotFoundException("user not found"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ResponseAuth auth = ResponseAuth.builder()
                .accessToken(PREFIX_HEADER_AUTH + token)
                .username(userDetails.getUsername())
                .expired(dateFormatter.format(times))
                .build();

        response.getWriter().write(signInController.login(userDetails.getUsername(), auth));
        response.setContentType("application/json");
        request.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.addHeader(HEADER_AUTH, PREFIX_HEADER_AUTH + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException, NotFoundException {
        if (failed instanceof BadCredentialsException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Username atau Password Anda salah");
        } else {
            getFailureHandler().onAuthenticationFailure(request, response, failed);
        }
    }
}
