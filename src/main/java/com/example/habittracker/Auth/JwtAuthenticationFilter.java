package com.example.habittracker.Auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenUtil tokenUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, TokenUtil tokenUtil) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Bỏ qua filter cho các đường dẫn /login và /register
        String requestPath = request.getServletPath();
        if (requestPath.equals("/login") || requestPath.equals("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenUtil.getTokenFromCookies(request);
        String userName = null;

        // Nếu không có token, chuyển hướng về /login
        if (token == null) {
            response.sendRedirect("/login");
            return;
        }

        // Kiểm tra token
        try {
            userName = jwtUtil.getUserNameFromToken(token);
        } catch (RuntimeException e) {
            // Token hết hạn hoặc không hợp lệ, chuyển hướng về /login
            response.sendRedirect("/login");
            return;
        }

        // Nếu token hợp lệ và chưa có Authentication trong SecurityContext
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
