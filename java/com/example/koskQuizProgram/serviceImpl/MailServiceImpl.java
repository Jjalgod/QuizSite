package com.example.koskQuizProgram.serviceImpl;

import com.example.koskQuizProgram.model.MailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl {
    private final JavaMailSender emailSender;
    String generatedString = RandomStringUtils.randomAlphanumeric(10);

    MailVO mailVO = new MailVO();
    public String sendSimpleMessage(MailVO mailVO, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ogyuchan01@gmail.com");
        message.setTo(email);
        message.setSubject("Welcome to Quiz Program!");
        message.setText("Authentication number : " + generatedString);
        emailSender.send(message);

        return generatedString;
    }

}
