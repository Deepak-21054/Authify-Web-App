package in.deepak.authify.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassowordRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "New Password is required")
    private String newPassword;
    @NotBlank(message = "OTP is required")
    private String otp;
}
