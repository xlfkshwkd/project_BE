package org.simsimhi.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.simsimhi.commons.Utils;
import org.simsimhi.commons.exceptions.BadRequestException;
import org.simsimhi.models.member.MemberInfo;
import org.simsimhi.models.member.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TokenProvider {

    private final String secret;
    private final long tokenValidityInSeconds;

    @Autowired
    private MemberInfoService infoService;

    private Key key;

    public TokenProvider(String secret, Long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;

        byte[] bytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Authentication authentication) {
        String authories = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date expires = new Date((new Date()).getTime() + tokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authories)
                .signWith(key, SignatureAlgorithm.HS512) // HMAC + SHA512  //분해 검증
                .setExpiration(expires)  //한시간
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getPayload();

        String email = claims.getSubject();
        MemberInfo userDetails = (MemberInfo) infoService.loadUserByUsername(email);

        String auth = claims.get("auth").toString();
        List<? extends GrantedAuthority> authorities = Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new).toList();
        userDetails.setAuthorities(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token, authorities);

        return authentication;
    }

    public void validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new BadRequestException(Utils.getMessage("EXPIRED.JWT_TOKEN", "validation"));
        } catch (UnsupportedJwtException e) {
            throw new BadRequestException(Utils.getMessage("UNSUPPORTED.JWT_TOKEN", "validation"));
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new BadRequestException(Utils.getMessage("INVALID_FORMAT.JWT_TOKEN", "validation"));
        }
    }
}