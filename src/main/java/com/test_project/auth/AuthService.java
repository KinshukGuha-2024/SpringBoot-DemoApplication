package com.test_project.auth;

import com.test_project.security.JwtService;
import com.test_project.test_project.User.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.test_project.test_project.User.User;
import com.test_project.test_project.User.User.UserStatus;
import com.test_project.test_project.User.User.VerificationStatus;

import java.security.SecureRandom;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final SecureRandom rnd = new SecureRandom();

    private AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(RegisterRequest request) {
        if(repo.existsByEmail(request.email())) throw new IllegalArgumentException("Email Already Exists");
        User u = new User();
        u.setFirstname(request.firstname());
        u.setLastname(request.lastname());
        u.setEmail(request.email());
        u.setPassword(encoder.encode(request.password())); // hash!
        u.setPhoneNumber(request.phoneNumber());
        u.setStatus(UserStatus.Inactive);
        u.setIsVerified(VerificationStatus.No);
        u.setOtp(generateOtp());

        repo.save(u);
    }

    public String generateOtp() {
        int n = rnd.nextInt(900000) + 100000; // 6 digits
        return String.valueOf(n);
    }
}
