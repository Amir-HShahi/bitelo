package dev.burgerman.bitelo.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.burgerman.bitelo.model.dto.ErrorResponse;
import dev.burgerman.bitelo.services.CustomUserDetailsService;
import dev.burgerman.bitelo.services.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String phoneNumber = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                phoneNumber = jwtService.extractCompletePhoneNumber(jwtToken);
            } catch (ExpiredJwtException e) {
                log.warn("JWT expired: {}", e.getMessage());
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired", request);
                return;
            } catch (IllegalArgumentException e) {
                log.error("Invalid JWT: {}", e.getMessage());
                writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid token", request);
                return;
            } catch (Exception e) {
                log.error("JWT parsing error", e);
                writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error while processing JWT token", request);
                return;
            }
        }

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
            if (jwtService.validateToken(jwtToken, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("JWT validation failed for phone: {}", phoneNumber);
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token validation failed",
                        request);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message,
            HttpServletRequest request)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                request.getRequestURI(),
                getOrGenerateTraceId(request));

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }

    private String getOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}
