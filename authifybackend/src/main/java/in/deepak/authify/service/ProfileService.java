package in.deepak.authify.service;

import in.deepak.authify.io.ProfileRequest;
import in.deepak.authify.io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest profileRequest);

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email,String otp,String newPassord);
     void sendOtp(String email);

     void verifyEmailOtp(String email,String otp);
}
