package in.deepak.authify.controller;

import in.deepak.authify.io.AuthRequest;
import in.deepak.authify.io.AuthResponse;
import in.deepak.authify.io.ResetPassowordRequest;
import in.deepak.authify.service.ProfileService;
import in.deepak.authify.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;
    @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
         try {
             authenticate(authRequest.getEmail(),authRequest.getPassword());
             final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
             final String jwtToken = jwtUtil.generateToken(userDetails);
             ResponseCookie cookie = ResponseCookie.from("jwt",jwtToken)
                     .httpOnly(true)
                     .path("/")
                     .maxAge(Duration.ofDays(1))
                     .sameSite("Strict")
                     .build();
             return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                     .body(new AuthResponse(authRequest.getEmail(),jwtToken));
         }
         catch (BadCredentialsException ex){
             Map<String,Object> error = new HashMap<>();
             error.put("error",true);
             error.put("message","Email or Password is Incorrect");
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
         }
         catch (DisabledException ex){
             Map<String,Object> error = new HashMap<>();
             error.put("error",true);
             error.put("message","Account is disabled");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
         }
         catch (Exception ex){
             Map<String,Object> error = new HashMap<>();
             error.put("error",true);
             error.put("message","Authentication is failed");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
         }
     }

     @PostMapping("/reset-password")
     public void resetPassword(@Valid @RequestBody ResetPassowordRequest request){
        try {
            profileService.resetPassword(request.getEmail(), request.getOtp(),request.getNewPassword());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
     }


    private void authenticate(String email, String password) {
         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse httpServletResponse){
        ResponseCookie responseCookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body("Logout successfully!");
    }

}
