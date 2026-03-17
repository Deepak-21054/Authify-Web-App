package in.deepak.authify.service.impl;

import in.deepak.authify.entity.UserEntity;
import in.deepak.authify.io.ProfileRequest;
import in.deepak.authify.io.ProfileResponse;
import in.deepak.authify.repository.UserRepository;
import in.deepak.authify.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest profileRequest) {
        UserEntity newProfile = convertToUserEntity(profileRequest);
        if(userRepository.existsByEmail(profileRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email Already exists");
        }
        newProfile = userRepository.save(newProfile);
        return convertToProfileResponse(newProfile);
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email " + email)
        );
        return convertToProfileResponse(userEntity);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email " + email)
        );
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expiryTime = System.currentTimeMillis() + (1000*60*15);

        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpiredAt(expiryTime);

        userRepository.save(existingEntity);

        try {
         emailService.sendResetOtp(existingEntity.getEmail(),otp);
        }catch (Exception e){
            throw new RuntimeException("Unable to send email");
        }

    }

    @Override
    public void resetPassword(String email, String otp, String newPassord) {
        UserEntity existingEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email " + email)
        );
        if(existingEntity.getResetOtp() == null || !existingEntity.getResetOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        if(existingEntity.getResetOtpExpiredAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }
        existingEntity.setPassword(passwordEncoder.encode(newPassord));
        existingEntity.setResetOtp(null);
        existingEntity.setResetOtpExpiredAt(0L);

        userRepository.save(existingEntity);

    }

    @Override
    public void sendOtp(String email){
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found" + email)
        );
        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
            return;
        }
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
        long expiryTime = System.currentTimeMillis() + (24*60*60*1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

        userRepository.save(existingUser);
      try {
        emailService.sendEmailVerificationOtp(existingUser.getEmail(), otp);
      }catch (Exception e){
          throw new RuntimeException("Unable to Send Email");
      }

    }

    @Override
    public void verifyEmailOtp(String email,String otp){
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found" + email)
        );

        try {
            if(existingUser != null && existingUser.getVerifyOtp() != null){

                if(!existingUser.getVerifyOtp().equals(otp)){
                    throw new RuntimeException("Otp is inCorrect");
                }

                if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
                    throw new RuntimeException("Opt is expired");
                }

            }else {
                throw new RuntimeException("Otp does not exist in Database");
            }

        }catch (Exception e){
           throw new RuntimeException(e.getMessage());
        }
        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        userRepository.save(existingUser);

    }


    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
     return ProfileResponse.builder()
             .email(newProfile.getEmail())
             .userId(newProfile.getUserId())
             .name(newProfile.getName())
             .isAccountVerified(newProfile.getIsAccountVerified())
             .build();
    }

    public UserEntity convertToUserEntity(ProfileRequest profileRequest) {
    return UserEntity.builder()
            .email(profileRequest.getEmail())
            .userId(UUID.randomUUID().toString())
            .name(profileRequest.getName())
            .password(passwordEncoder.encode(profileRequest.getPassword()))
            .isAccountVerified(false)
            .resetOtpExpiredAt(0L)
            .verifyOtp(null)
            .verifyOtpExpireAt(0L)
            .resetOtp(null)
            .build();
    }

}
