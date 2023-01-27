package com.keeper.homepage.global.util.mail;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

  private final JavaMailSender javaMailSender;

  public void sendMail(List<String> toUserList, String subject, String text) {
    SimpleMailMessage simpleMessage = new SimpleMailMessage();
    simpleMessage.setTo(toUserList.toArray(new String[toUserList.size()]));
    simpleMessage.setSubject(subject);
    simpleMessage.setText(text);
    javaMailSender.send(simpleMessage);
  }
}
