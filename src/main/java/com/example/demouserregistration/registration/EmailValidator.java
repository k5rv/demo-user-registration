package com.example.demouserregistration.registration;

import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

  @Override
  public boolean test(String email) {
    boolean isValid = true;
    try {
      InternetAddress internetAddress = new InternetAddress(email);
      internetAddress.validate();
    } catch (AddressException e) {
      isValid = false;
    }
    return isValid;
  }
}
