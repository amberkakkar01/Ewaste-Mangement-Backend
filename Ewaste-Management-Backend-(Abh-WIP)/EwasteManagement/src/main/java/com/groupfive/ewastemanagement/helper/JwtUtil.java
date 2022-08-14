package com.groupfive.ewastemanagement.helper;

import com.groupfive.ewastemanagement.entity.Role;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.InvalidTokenException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private final UserRepo userRepo;

    @Autowired
    public JwtUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public static final long JWT_TOKEN_VALIDITY = 10 * 60 * 1000L;

    public static final long JWT_REFRESH_TOKEN_VALIDITY = 30 * 60 * 1000L;

    public static final long JWT_PASSWORD_RESET_TOKEN_VALIDITY = 7200000L;

    private static final String SECRET = "eWaste";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * This method is used to extract data from the token
     *
     * @param token String Parameter
     * @return Extracted data
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * This method is used to check if person has owner/ write permission for the request
     *
     * @param token          String parameter
     * @param claimsResolver Claim Parameter
     * @return claims:if user have permission for the request
     */

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    /**
     * This method is used to check the token's validity
     *
     * @param token String Parameter
     * @return boolean
     */

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        User user = userRepo.findUserByEmail(userPrincipal.getEmail());

        for (Role role : user.getRoles()) {
            if (role.getName().equals(Constants.COLLECTOR)) {
                claims.put(Constants.IS_COLLECTOR, true);
            }
            if (role.getName().equals(Constants.CUSTOMER)) {
                claims.put(Constants.IS_CUSTOMER, true);
            }
            if (role.getName().equals(Constants.VENDOR)) {
                claims.put(Constants.IS_VENDOR, true);
            }
        }


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

    }


    /**
     * This method is used to fetch GoogleId from the token
     *
     * @param token String Parameter
     * @return GoogleId
     * @throws InvalidUserException
     */

    public String fetchId(String token) {
        LOGGER.info("Fetching google id from JWT");

        Claims claims = extractDataFromToken(token);
        String googleId = claims.getSubject();

        if (userRepo.findUserByEmail(googleId) != null) {
            LOGGER.info("Id fetched");
            return googleId;
        } else {
            LOGGER.error("Illegal access, google id '{}' is not registered", googleId);
            throw new InvalidUserException("Illegal access");
        }
    }

    /**
     * This method is used to extract data from the token
     *
     * @param token String Parameter
     * @return Extracted data
     */

    public Claims extractDataFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.error("The JWT is invalid or expired");
            throw new InvalidTokenException("The JWT is invalid or expired");
        }
    }

    /**
     * This method is used to generate JWT token for signIn
     *
     * @param userDetails to access username and password of the user
     * @return token
     */

    public Map<String, String> generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority(Constants.COLLECTOR))) {
            claims.put(Constants.IS_COLLECTOR, true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.CUSTOMER))) {
            claims.put(Constants.IS_CUSTOMER, true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.VENDOR))) {
            claims.put(Constants.IS_VENDOR, true);
        }

        return doGenerateToken(claims, userDetails.getUsername());
    }

    public Map<String, String> createTokenFromUserName(String username, User user) {

        Claims claims = Jwts.claims().setSubject(username);
        Role role = user.getRoles().stream().iterator().next();

        if (role.getName().equals(Constants.COLLECTOR)) {
            claims.put(Constants.IS_COLLECTOR, true);
        }
        if (role.getName().equals(Constants.CUSTOMER)) {
            claims.put(Constants.IS_CUSTOMER, true);
        }
        if (role.getName().equals(Constants.VENDOR)) {
            claims.put(Constants.IS_VENDOR, true);
        }

        String accessToken = Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(new Date())//
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))//
                .signWith(SignatureAlgorithm.HS256, SECRET)//
                .compact();

        String refreshToken = Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(new Date())//
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))//
                .signWith(SignatureAlgorithm.HS256, SECRET)//
                .compact();

        HashMap<String, String> token = new HashMap<>();
        token.put("Access-Token", accessToken);
        token.put("Refresh-Token", refreshToken);

        return token;
    }

    /**
     * This method is used to generate JWT token for password reset
     *
     * @param userDetails to access username and password of the user
     * @return token
     */

    public String generatePasswordResetToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority(Constants.COLLECTOR))) {
            claims.put(Constants.IS_COLLECTOR, true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.CUSTOMER))) {
            claims.put(Constants.IS_CUSTOMER, true);
        }
        if (roles.contains(new SimpleGrantedAuthority(Constants.VENDOR))) {
            claims.put(Constants.IS_VENDOR, true);
        }

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_PASSWORD_RESET_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    private HashMap<String, String> doGenerateToken(Map<String, Object> claims, String subject) {

        String accessToken = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        String refreshToken = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        HashMap<String, String> token = new HashMap<>();
        token.put("Access-Token", accessToken);
        token.put("Refresh-Token", refreshToken);

        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}