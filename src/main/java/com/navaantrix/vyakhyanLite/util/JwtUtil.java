package com.navaantrix.vyakhyanLite.util;

import com.navaantrix.vyakhyanLite.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

@AllArgsConstructor
@Service
public class JwtUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //  Decode JWT payload (no validation)
    private Map<String, Object> decodePayload(String token) {
        try {
            if(token.isEmpty()){
                throw new DataNotFoundException("Token is not Add Or Token is not found. " + "Jwt Util + Decode Payload");
            }
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Invalid JWT token");
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payload, Map.class);

        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Error decoding JWT", e);
        }
    }

    //  Extract SUB (your main need)
    public String extractUserId(String token) {
        try {
            if(token.isEmpty()){
                throw new DataNotFoundException("Token is not Add Or Token is not found" + "JWT Util _ extractUserId");
            }
            Map<String, Object> claims = decodePayload(token);
            return (String) claims.get("sub");
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Extract any claim
    public Object extractClaim(String token, String key) {
        try {
            if(token.isEmpty()){
                throw new DataNotFoundException("Token is not Add Or Token is not found" + "JWT Util _ extractClaim");
            }
            Map<String, Object> claims = decodePayload(token);
            return claims.get(key);
        } catch (DataNotFoundException e ){
            throw new DataNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Extract username
    public String extractUsername(String token) {
        return (String) extractClaim(token, "preferred_username");
    }

    // Extract email
    public String extractEmail(String token) {
        return (String) extractClaim(token, "email");
    }

    // Extract expiration
    public Long extractExpiration(String token) {
        return (Long) extractClaim(token, "exp");
    }

    //Extract roles (Keycloak format)
    public Object extractRoles(String token) {
        Map<String, Object> claims = decodePayload(token);
        return claims.get("resource_access");
    }

}
