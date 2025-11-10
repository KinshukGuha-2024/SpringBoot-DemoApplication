package com.test_project.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.Map;


@Service
public class OtpMailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:no-reply@example.com}")
    private String from;

    @Value("${app.otp.exp-minutes:10}")
    private String defaultExpiryMinutes;

    public OtpMailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String name, String otp) {
        sendOtpEmail(to, name, otp, Integer.parseInt(defaultExpiryMinutes), "Demo");
    }

    public void sendOtpEmail(String to, String name, String otp, int expiresInMinutes, String brand) {
        Context ctx = new Context();
        ctx.setVariables(Map.of(
                "name", name,
                "otp", otp,
                "expiresIn", expiresInMinutes,
                "brand", brand,
                "year", Year.now().toString()
        ));

        String html = templateEngine.process("otp-email", ctx);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your " + brand + " OTP Code");
            helper.setText(html, true);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send otp", e);
        }
    }
}
