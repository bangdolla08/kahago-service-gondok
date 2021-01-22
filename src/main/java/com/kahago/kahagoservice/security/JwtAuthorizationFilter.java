package com.kahago.kahagoservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.kahago.kahagoservice.security.SecurityConstant.*;

/**
 * @author Hendro yuwono
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserServiceSecurity userServiceSecurity;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserServiceSecurity service) {
        super(authenticationManager);
        this.userServiceSecurity = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_AUTH);
        if (header == null || !header.startsWith(PREFIX_HEADER_AUTH)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header invalid");
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken = authenticationToken(request);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token Expired");
        }
    }

    private UsernamePasswordAuthenticationToken authenticationToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTH);
        if (token == null) {
            return null;
        }

        String username = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.replace(PREFIX_HEADER_AUTH, ""))
                .getBody()
                .getSubject();
        if (username == null) {
            return null;
        }

        UserDetails userDetails = userServiceSecurity.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
