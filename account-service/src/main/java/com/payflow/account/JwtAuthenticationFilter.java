package com.payflow.account;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * This filter intercepts EVERY HTTP request.
 * It runs before your controller methods.
 *
 * What it does:
 * 1. Look for Authorization header: "Bearer <token>"
 * 2. Extract the token
 * 3. Validate the token
 * 4. If valid → set the user as authenticated
 * 5. If invalid/missing → do nothing (Spring Security blocks it)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // get the Authorization header
        // it looks like: "Bearer eyJhbGci..."
        final String authHeader = request.getHeader("Authorization");

        // if no Authorization header or doesn't start with "Bearer "
        // skip this filter and move on
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract the token — remove "Bearer " prefix
        final String jwt = authHeader.substring(7);

        // extract email from token
        final String email = jwtService.extractEmail(jwt);

        // if we got an email and user isn't already authenticated
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // validate the token
            if (jwtService.isTokenValid(jwt, email)) {

                // create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // set user as authenticated in Spring Security context
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

                log.debug("Authenticated user: {}", email);
            }
        }

        // continue with the request
        filterChain.doFilter(request, response);
    }
}