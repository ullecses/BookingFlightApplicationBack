package org.flightbooking;

import org.flightbooking.services.JwtService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter implements Filter {

    private final JwtService jwtService = new JwtService(); // Инстанс JwtService

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Можно оставить пустым
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7); // Убираем "Bearer "
            String email = jwtService.extractEmail(jwt);

            if (email != null && jwtService.isTokenValid(jwt, email)) {
                // Если токен валиден, передаем управление дальше
                chain.doFilter(request, response);
                return;
            }
        }

        // Если токен невалиден или отсутствует
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write("Unauthorized");
    }

    @Override
    public void destroy() {
        // Можно оставить пустым
    }
}
