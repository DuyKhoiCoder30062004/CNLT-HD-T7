package com.example.ecommerce.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	public String generateToken(String username){
		return Jwts.builder()
				.subject(username)
				.issuedAt(new Date())
				.expiration( new Date(System.currentTimeMillis()+ 86400000))
				.signWith(getKey())
				.compact();
	}

	public String extractPhone(String token){
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	public boolean isValid(String token){
		try{
			Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token);
			return true;
		}
		catch( Exception e){
			return false;
		}
	}

	private SecretKey getKey(){
		return Keys.hmacShaKeyFor(
			SECRET_KEY.getBytes(StandardCharsets.UTF_8)
		);
	}
}
