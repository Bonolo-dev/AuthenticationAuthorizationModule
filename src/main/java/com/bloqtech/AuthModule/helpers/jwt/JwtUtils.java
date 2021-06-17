package com.bloqtech.AuthModule.helpers.jwt;

import com.bloqtech.AuthModule.helpers.PsiberUserDetails;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.header}")
    private String jwtHeader;

    @Value("${jwt.prefix}")
    private String jwtPrefix;


    public String generateJwtToken(Authentication authentication){

        PsiberUserDetails psiberUserDetails = (PsiberUserDetails)authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((psiberUserDetails.getUsername()))
                .claim("authorities",psiberUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, this.getSecretKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(this.getSecretKey()).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(this.getSecretKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    private String getSecretEncode(){
        Key secret = new SecretKeySpec(jwtSecret.getBytes(),0,jwtSecret.getBytes().length, "DES");
        byte[] secretBytes = secret.getEncoded();
        return Base64.getEncoder().encodeToString(secretBytes);
    }

    private Key getSecretKey(){
        byte[] secretByte=Base64.getEncoder().encode(jwtSecret.getBytes());
        return new SecretKeySpec(secretByte,0,secretByte.length, "DES");
    }
    public String getJwtHeader(){
        return this.jwtHeader;
    }
    public String getJwtPrefix(){
        return this.jwtPrefix;
    }
}
