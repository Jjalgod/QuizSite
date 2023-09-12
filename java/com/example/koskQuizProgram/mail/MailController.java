package com.example.koskQuizProgram.mail;

import com.example.koskQuizProgram.model.MailVO;
import com.example.koskQuizProgram.serviceImpl.MailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MailController {

    MailVO mailVO = new MailVO();
    private MailServiceImpl mailService;

    public MailController(MailServiceImpl mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/send/mail")
    public String mail(
            @RequestParam("email") String email
        ) {
        String res = mailService.sendSimpleMessage(mailVO, email);
        return res;
    }
}
