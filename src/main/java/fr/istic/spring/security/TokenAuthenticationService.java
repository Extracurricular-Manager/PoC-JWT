package fr.istic.spring.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class TokenAuthenticationService {

    static final long EXPIRATION_TIME = 864_000_000; // 10 jours
    static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    static final String AUTHORITIES_KEY = "scopes";

    public static void addAuthentication(HttpServletResponse res, Authentication auth) {
        final String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        final String jwt = Jwts.builder().setSubject(auth.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY).compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        if(!request.getRequestURI().contains("/login")) {
            final String token = request.getHeader(HEADER_STRING);
            if (token != null) {
                try {
                    final var user = Jwts.parserBuilder().setSigningKey(KEY)
                            .build()
                            .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                            .getBody();
                    final Collection<? extends GrantedAuthority> authorities =
                            Arrays.stream(user.get(AUTHORITIES_KEY).toString().split(","))
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());
                    return user.getSubject() != null ? new UsernamePasswordAuthenticationToken(user.getSubject(), null, authorities) : null;
                } catch (SignatureException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private TokenAuthenticationService() {}

}
