package com.example.demouserregistration.registration;

import com.example.demouserregistration.appuser.AppUser;
import com.example.demouserregistration.appuser.AppUserRole;
import com.example.demouserregistration.appuser.AppUserService;
import com.example.demouserregistration.registration.token.ConfirmationToken;
import com.example.demouserregistration.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

  private static final String EMAIL_NOT_VALID_MSG = "email %s not valid";
  private static final String TOKEN_ALREADY_USED = "token %s already used";
  private static final String TOKEN_EXPIRED = "token %s expired";
  private static final String TOKEN_NOT_FOUND_MSG = "token %s not found";
  private final EmailValidator emailValidator;
  private final AppUserService appUserService;
  private final ConfirmationTokenService confirmationTokenService;

  public String register(RegistrationRequest request) {
    boolean isValidEmail = emailValidator.test(request.getEmail());
    if (!isValidEmail) {
      throw new IllegalStateException(String.format(EMAIL_NOT_VALID_MSG, request.getEmail()));
    }
    return appUserService.signUpUser(
        new AppUser(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            AppUserRole.USER));
  }

  @Transactional
  public String confirmToken(String token) {
    ConfirmationToken confirmationToken =
        confirmationTokenService
            .getToken(token)
            .orElseThrow(
                () -> new IllegalStateException(String.format(TOKEN_NOT_FOUND_MSG, token)));
    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException(String.format(TOKEN_ALREADY_USED, token));
    }
    if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException(String.format(TOKEN_EXPIRED, token));
    }
    confirmationTokenService.setConfirmedAt(token);
    appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
    return "confirmed";
  }
}
