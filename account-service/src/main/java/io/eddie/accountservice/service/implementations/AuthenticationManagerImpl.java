package io.eddie.accountservice.service.implementations;

import io.eddie.accountservice.mapper.AuthenticationMapper;
import io.eddie.accountservice.model.dto.AuthenticationDetails;
import io.eddie.accountservice.service.AccountService;
import io.eddie.accountservice.service.AuthenticationManager;
import io.eddie.accountservice.service.TokenProvider;
import io.eddie.core.model.vo.TokenBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationManagerImpl implements AuthenticationManager {

    @Value("${custom.jwt.expiration}")
    private Long availableTime;
    private final TokenProvider tokenProvider;

    private final AccountService accountService;

    @Override
    public AuthenticationDetails loadAuthenticationByCode(String accountCode) {
        return AuthenticationMapper.toDetails(accountService.getByAccountCode(accountCode));
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validate(token);
    }

    @Override
    public TokenBody parseToken(String token) {
        return tokenProvider.parse(token);
    }

    @Override
    public String issueToken(String code) {
        return tokenProvider.issue(availableTime, Map.of("accountCode", code));
    }


}
