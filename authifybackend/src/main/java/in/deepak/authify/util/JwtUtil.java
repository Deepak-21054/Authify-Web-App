package in.deepak.authify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${jwt.secrect.key}")
    private String SECRECT_KEY;


    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String email) {
       return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*2))
                .signWith(SignatureAlgorithm.HS256,SECRECT_KEY).compact();
    }

    private Claims extractAllCliams(String token){
        return Jwts.parser().setSigningKey(SECRECT_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token){
        return extractAllCliams(token).getExpiration();
    }

    public String extractEmail(String token){
        return extractAllCliams(token).getSubject();
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        String email = extractEmail(token);
        System.out.println("is token valid "+email);
        if(userDetails.getUsername().equals(email) && !isTokenExpired(token)){
            System.out.println("in the if "+email + " "+isTokenExpired(token));
            return true;
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


}
