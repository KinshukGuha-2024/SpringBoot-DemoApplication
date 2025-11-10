package com.test_project.test_project.User;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    public String firstname;

    @Column(nullable = false)
    public String lastname;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column(nullable = false)
    public String phoneNumber;

    @Column(nullable = true)
    public String otp;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private VerificationStatus isVerified = VerificationStatus.No;

    public enum VerificationStatus {
        Yes,
        No
    }

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.Inactive;

    public enum UserStatus {
        Active,
        Inactive,
        Suspended,

    }

    public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public VerificationStatus getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(VerificationStatus isVerified) {
        this.isVerified = isVerified;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
