package com.sk.SpringSecurity.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.User;
import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final String SECRET_KEY = "mySuperSecretJwtKeyForBankApplication2026";

	private Key getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );
    }
	public String generateToken(User user) {
		
		Map<String,Object> claims = new HashMap<>();
		
		claims.put("role",user.getRole().name());
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(user.getEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
				.signWith(getSigningKey())
				.compact();
	}
	
	public String extractUsername(String token) {

	    try {

	        return Jwts.parser()
	                .setSigningKey(getSigningKey())
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject();

	    } catch(Exception e) {

	        System.out.println("JWT ERROR =================");
	        e.printStackTrace();

	        return null;
	    }
	}
	
	public String extractRole(String token) {

	    try {

	        return Jwts.parser()
	                .setSigningKey(getSigningKey())
	                .parseClaimsJws(token)
	                .getBody()
	                .get("role", String.class);

	    } catch(Exception e) {

	        System.out.println("JWT ROLE ERROR =================");
	        e.printStackTrace();

	        return null;
	    }
	}
}
