package in.deepak.authify.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "User name cannot be null")
    private String name;
    @NotBlank(message = "User email cannot be null")
    @Email(message = "email should follow standard email format")
    private String email;
    @Size(min = 6,message = "password should have atleast 6 characters")
    private String password;
}
