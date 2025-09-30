package org.example.notificationservice.service.delivery;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Component
@Slf4j
public class EmailDelivery {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailDelivery(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public Mono<Void> sendEmail(String email, String subject, String body) {
        return Mono.fromRunnable(() -> {
            try {
                Context context = new Context();
                context.setVariable("subject", subject);
                context.setVariable("body", body);

                String htmlContent = templateEngine.process("email-template", context);

                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                javaMailSender.send(message);
                log.info("Sending HTML email to {} with subject '{}'", email, subject);
            } catch (MessagingException e) {
                log.error("Error sending email to {}: {}", email, e.getMessage());
            }
        });
    }
}

