package in.deepak.authify.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail,String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our platform");
        message.setText("Hello "+name+", "+"\n\nThanks for registering with us\n\nRegards Authify team");
        javaMailSender.send(message);
    }

    public void sendResetOtp(String email,String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Password Reset Otp");
        message.setText("Your otp for resetting password "+otp+" use this otp for proceeding further");
        javaMailSender.send(message);
    }

    public void sendEmailVerificationOtp(String receiver,String generatedOtp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(receiver);
        message.setSubject("Verification Otp");
        message.setText("Your otp for verification "+generatedOtp+" use this otp for proceeding further");
        javaMailSender.send(message);
    }


}
