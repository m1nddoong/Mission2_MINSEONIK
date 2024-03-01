package com.example.market.shop.service;


import com.example.market.shop.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender javaMailSender;

    public void sendEmail(String ownerEmail, EmailDto dto) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(ownerEmail);
            helper.setSubject(dto.getSubject());
            helper.setText(dto.getText());
        } catch (MessagingException e) {
            throw new RuntimeException("이메일이 전송되지 않았습니다.", e);
        }
        javaMailSender.send(mimeMessage);
    }

}
