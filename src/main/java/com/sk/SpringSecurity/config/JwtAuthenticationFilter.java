package com.sk.SpringSecurity.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sk.SpringSecurity.service.CustomUserDetailsService;
import com.sk.SpringSecurity.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        
        if (request.getServletPath().startsWith("/auth")) {

            filterChain.doFilter(request, response);
            return;
        }



        String authHeader =
                request.getHeader("Authorization");



        
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {


            filterChain.doFilter(request, response);
            return;
        }



        
        String token =
                authHeader.substring(7);



        
        String email =
                jwtService.extractUsername(token);



        
        if (email == null) {


            SecurityContextHolder.clearContext();


            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );


            response.getWriter()
                    .write("Invalid or Expired Token");


            return;
        }




        
        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {



            UserDetails userDetails =
                    customUserDetailsService
                            .loadUserByUsername(email);



            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(

                            userDetails,

                            null,

                            userDetails.getAuthorities()

                    );



            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);

        }



        filterChain.doFilter(
                request,
                response
        );

    }
}