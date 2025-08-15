package api.giybat.uz.utils;

import api.giybat.uz.dto.JwtDTO;
import api.giybat.uz.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final int tokenLiveTime = 1000 * 3600 * 24; // 1-day
    private static final long refreshTokenLiveTime = 1000L * 3600 * 24 * 30; // 30-days
    @Value("${jwt.secretKey}")
    private static String secretKey;

    /**
     * General
     */
    public static String encode(String username, ProfileRole role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role.name());

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Token decode qilish (JWT dan username va rolni olish)
     */
    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        String roleStr = (String) claims.get("role");
        ProfileRole role = ProfileRole.valueOf(roleStr);

        return new JwtDTO(username, claims.get("code").toString(), role);
    }

    /**
     * Ro'yxatdan o'tish tokeni
     */
    public static String encodeForRegistration(String username, String code) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("code", code);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Ro'yxatdan o'tish tokenini ochish
     */
    public static JwtDTO decodeRegistrationToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtDTO(claims.getSubject(), claims.get("code").toString());
    }

    public static boolean isTokenExpired(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .after(new Date());
    }

    public static String refreshToken(String phone, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(phone)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenLiveTime))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Kalitni olish (256 bitli kalit)
     */
    private static SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
