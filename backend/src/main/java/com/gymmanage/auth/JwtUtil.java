package com.gymmanage.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymmanage.common.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final ObjectMapper objectMapper;

    private final String secret;
    private final long ttlSeconds;

    public JwtUtil(
            ObjectMapper objectMapper,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.ttl-seconds}") long ttlSeconds
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.ttlSeconds = ttlSeconds;
    }

    public String generate(String subject) {
        long now = Instant.now().getEpochSecond();
        long exp = now + ttlSeconds;

        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", subject);
        payload.put("iat", now);
        payload.put("exp", exp);

        try {
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headerB64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signingInput = headerB64 + "." + payloadB64;
            String sigB64 = base64UrlEncode(hmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), secret));
            return signingInput + "." + sigB64;
        } catch (Exception e) {
            throw BizException.badRequest("failed to generate token");
        }
    }

    public JwtClaims verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw BizException.unauthorized("invalid token");
        }

        String signingInput = parts[0] + "." + parts[1];
        String sigProvided = parts[2];
        String sigExpected = base64UrlEncode(hmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), secret));
        if (!MessageDigest.isEqual(sigExpected.getBytes(StandardCharsets.UTF_8), sigProvided.getBytes(StandardCharsets.UTF_8))) {
            throw BizException.unauthorized("invalid token signature");
        }

        try {
            byte[] payloadBytes = base64UrlDecode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<Map<String, Object>>() {});
            String sub = (String) payload.get("sub");
            long exp = asLong(payload.get("exp"));
            long now = Instant.now().getEpochSecond();
            if (exp < now) {
                throw BizException.unauthorized("token expired");
            }
            long iat = asLong(payload.get("iat"));
            return new JwtClaims(sub, iat, exp);
        } catch (BizException ex) {
            throw ex;
        } catch (Exception e) {
            throw BizException.unauthorized("invalid token payload");
        }
    }

    private static long asLong(Object v) {
        if (v instanceof Integer i) {
            return i.longValue();
        }
        if (v instanceof Long l) {
            return l;
        }
        if (v instanceof Double d) {
            return d.longValue();
        }
        if (v instanceof String s) {
            return Long.parseLong(s);
        }
        throw new IllegalArgumentException("invalid number");
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String s) {
        return Base64.getUrlDecoder().decode(s);
    }

    private static byte[] hmacSha256(byte[] data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("hmac error", e);
        }
    }

    public static class JwtClaims {
        private final String subject;
        private final long iat;
        private final long exp;

        public JwtClaims(String subject, long iat, long exp) {
            this.subject = subject;
            this.iat = iat;
            this.exp = exp;
        }

        public String getSubject() {
            return subject;
        }

        public long getIat() {
            return iat;
        }

        public long getExp() {
            return exp;
        }
    }
}

