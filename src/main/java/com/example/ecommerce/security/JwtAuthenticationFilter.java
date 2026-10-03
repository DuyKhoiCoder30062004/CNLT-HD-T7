package com.example.ecommerce.security;

import java.io.IOException;
import java.util.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService){
		this.jwtService=jwtService;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException{

		String authHeader = request.getHeader("Authorization");
		if (authHeader==null || !authHeader.startsWith("Bearer")){
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		if (jwtService.isValid(token)){
			String phone = jwtService.extractPhone(token);
			UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(
					phone,
					null,
					Collections.emptyList()
				);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}
