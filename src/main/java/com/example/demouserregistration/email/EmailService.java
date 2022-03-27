package com.example.demouserregistration.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
  private final JavaMailSender mailSender;
  private static final String FAILED_TO_SEND_EMAIL_MSG = "failed to send email to %s";

  @Override
  @Async
  public void send(String to, String email) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setText(email, true);
      helper.setTo(to);
      helper.setSubject("Confirm your email");
      helper.setFrom("hi@springboot.com");
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      LOGGER.error(String.format(FAILED_TO_SEND_EMAIL_MSG, email), e);
      throw new IllegalStateException(String.format(FAILED_TO_SEND_EMAIL_MSG, email));
    }
  }
}
