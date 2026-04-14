package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.authservice.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.validity}")
    private Long validity ;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.cookie}")
    private String jwtCookie;

    private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //Generate JWT token fromusername

    public String genJwtFromUsername(UserDetails user) {
        String username = user.getUsername();
        Date expiry = new Date(System.currentTimeMillis() + validity);
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .setExpiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    //Genrate Signing Key
    public Key signingKey(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    /*//Get token from request headers
    public String getTokenFromRequestHeads(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorisation Header: {}", bearerToken);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }*/

    //Get token from Cookie
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null){
            return cookie.getValue();
        }else{
            return null;
        }
    }

    //Generate JwtCookie upon successful signin
    public ResponseCookie generateJwtCookie(UserDetailsImpl userdDets) {
        String token = genJwtFromUsername(userdDets);
        return ResponseCookie.from( jwtCookie, token)
                .path("/api")//limits the cookie access to /api routes only
                .maxAge(24*60*60)
                .httpOnly(false)//allows Javascript access to this cookie
                .build();
    }

    //Generate clean cookie - for signout
    public ResponseCookie replaceJwtcookieWithCleanCookie() {
        return ResponseCookie.from( jwtCookie, null)
                .path("/api")//limits the cookie access to /api routes only
                .build();
    }
    //Get username from Token
    public String usernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey())
                    .build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired");
        } catch (UnsupportedJwtException e) {
            logger.error("Token is not supported by this JWT");
        } catch (MalformedJwtException e) {
            logger.error("Token has expired");
        } catch (SecurityException e) {
            logger.error("Token has expired");
        } catch (IllegalArgumentException e) {
            logger.error("Jwt claims string is empty");
        }
        return false;
    }
}
