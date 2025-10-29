package io.eddie.accountservice.service;


import io.eddie.accountservice.model.dto.AuthenticationDetails;
import io.eddie.core.model.vo.TokenBody;

public interface AuthenticationManager {

    AuthenticationDetails loadAuthenticationByCode(String accountCode);

    boolean validateToken(String token);
    TokenBody parseToken(String token);
    String issueToken(String code);


}
