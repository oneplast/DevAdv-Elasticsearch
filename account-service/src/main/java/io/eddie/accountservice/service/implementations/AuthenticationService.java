package io.eddie.accountservice.service.implementations;

import io.eddie.accountservice.mapper.AuthenticationMapper;
import io.eddie.accountservice.model.entity.Account;
import io.eddie.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account = accountService.getByUsername(username);

        return AuthenticationMapper.toDetails(account);

    }

}
