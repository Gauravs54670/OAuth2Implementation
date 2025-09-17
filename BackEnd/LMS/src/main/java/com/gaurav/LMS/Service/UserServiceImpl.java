package com.gaurav.LMS.Service;

import com.gaurav.LMS.DTO.ForgotPasswordRequest;
import com.gaurav.LMS.DTO.SignUpDTO;
import com.gaurav.LMS.DTO.UserDTO;
import com.gaurav.LMS.Entity.UserEntity;
import com.gaurav.LMS.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    public UserServiceImpl(
            JavaMailSender javaMailSender,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String registerUser(SignUpDTO signUpDTO) {
        boolean exist = this.userRepository.findByEmail(signUpDTO.getEmail()).isPresent();
        if(exist) throw new RuntimeException("User is already registered.");
        UserEntity user = UserEntity.builder()
                .email(signUpDTO.getEmail())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .build();
        user = this.userRepository.save(user);
        return "Registration Successful";
    }

    @Override
    public UserDTO getDetails(String email) {
        UserEntity user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .contact(user.getContact())
                .bio(user.getBio())
                .build();
    }

    @Override
    public UserDTO updateDetails(String email, UserDTO userDTO) {
        UserEntity user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(userDTO.getEmail() != null && !userDTO.getEmail().isEmpty())
            throw new RuntimeException("Email can not be updated.");
        if(userDTO.getFirstName() != null && !userDTO.getFirstName().isEmpty())
            user.setFirstName(userDTO.getFirstName());
        if(userDTO.getLastName() != null && !userDTO.getLastName().isEmpty())
            user.setLastName(userDTO.getLastName());
        if(userDTO.getBio() != null && !userDTO.getBio().isEmpty())
            user.setBio(userDTO.getBio());
        if(userDTO.getContact() != null && !userDTO.getContact().isEmpty())
            user.setContact(userDTO.getContact());
        user = this.userRepository.save(user);
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .contact(user.getContact())
                .bio(user.getBio())
                .build();
    }

    @Override
    public String forgotPasswordRequest(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            // Generate secure token (OTP style)
            String token = generateOtpToken(); // 6-digit OTP
            Instant tokenExpirationTime = Instant.now().plus(5, ChronoUnit.MINUTES);
            user.setResetToken(token);
            user.setTokenExpirationTime(tokenExpirationTime);
            this.userRepository.save(user);
            SimpleMailMessage mailMessage = buildResetMail(user, token);
            javaMailSender.send(mailMessage);
            log.info("Password reset email sent to {}", user.getEmail());
        }
        // Always return generic response to avoid user enumeration
        return "If the email is registered, a reset code has been sent.";
    }

    @Override
    public String updatePasswordRequest(String email, ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity user = userRepository.findByResetToken(forgotPasswordRequest.getForgotPasswordToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token."));
        if (!email.equals(user.getEmail())) {
            throw new AccessDeniedException("Invalid request.");
        }
        if (Instant.now().isAfter(user.getTokenExpirationTime())) {
            throw new RuntimeException("Reset token expired. Please request a new one.");
        }
        user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpirationTime(null);
        userRepository.save(user);
        return "Password changed successfully.";
    }

    @Override
    public String changePassword(String email, String currentPassword, String newPassword) {
        UserEntity user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid request. Please try again"));
        boolean isMatched = passwordEncoder.matches(currentPassword, user.getPassword());
        if(!isMatched) throw new BadCredentialsException("Password not matched. Please try again or click on forgot password");
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(newEncodedPassword);
        userRepository.save(user);
        return "Password changed successfully";
    }

    // ---- Token Generators ----
    /**
     * Generates a cryptographically secure 6-digit OTP.
     */
    private String generateOtpToken() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // ensures 6 digits
        return String.valueOf(otp);
    }
    // ---- Email Builder ----
    private static SimpleMailMessage buildResetMail(UserEntity user, String token) {
        String message = String.format(
                """
                        Hi %s %s,
                        
                        We received a request to reset your LMS account password.
                        
                        Your reset code is: %s
                        
                        This code will expire in 5 minutes.
                        
                        If you didn’t request a password reset, please ignore this email.""",
                user.getFirstName(), user.getLastName(), token
        );

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(user.getEmail());
        mail.setSubject("Password Reset Request");
        mail.setText(message);

        return mail;
    }

}
