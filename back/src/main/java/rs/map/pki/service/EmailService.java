package rs.map.pki.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendActivationEmail(String to, String token) {
        String activationLink = "https://localhost:8080/auth/activate?token=" + token;

        String subject = "Account Activation - PKI System";
        String htmlMessage = "<p>Hello,</p>"
                + "<p>Click the link below to activate your account:</p>"
                + "<p><a href=\"" + activationLink + "\">Activate Account</a></p>"
                + "<p>This link will expire in 24 hours.</p>";

        sendMimeEmail(to, subject, htmlMessage);
    }

    private void sendMimeEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Greška prilikom slanja mejla", e);
        }
    }
}
