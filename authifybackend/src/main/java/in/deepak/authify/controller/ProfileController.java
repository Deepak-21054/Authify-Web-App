package in.deepak.authify.controller;

import in.deepak.authify.io.ProfileRequest;
import in.deepak.authify.io.ProfileResponse;
import in.deepak.authify.service.ProfileService;
import in.deepak.authify.service.impl.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest profileRequest){
        ProfileResponse response = profileService.createProfile(profileRequest);
       emailService.sendWelcomeEmail(response.getEmail(),response.getName());
        return response;
    }

    @GetMapping("/test")
    public String test(){
        return "test is working";
    }

    @GetMapping("/profile")
    public ProfileResponse getProfiler(@CurrentSecurityContext(expression = "authentication?.name") String email){
         return profileService.getProfile(email);
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAccountAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email != null);
    }

    @PostMapping("/reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try {
            profileService.sendResetOtp(email);
        }catch (Exception e){
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/email-verify-otp")
    public void sendEmailVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try {
            profileService.sendOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public void verifyEmailOtp(@RequestBody Map<String,Object> request, @CurrentSecurityContext(expression = "authentication?.name") String email){
        try {
            if(request.get("otp").toString() == null){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Missing Otp");
            }
            profileService.verifyEmailOtp(email, request.get("otp").toString());
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

}
