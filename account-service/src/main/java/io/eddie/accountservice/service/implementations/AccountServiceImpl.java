package io.eddie.accountservice.service.implementations;

import io.eddie.accountservice.model.dto.CreateAccountRequest;
import io.eddie.accountservice.model.entity.Account;
import io.eddie.accountservice.repository.AccountRepository;
import io.eddie.accountservice.service.AccountService;
import io.eddie.core.events.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Value("${accounts.events.topic.name}")
    private String accountsEventsTopicName;

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public void applyCartCode(String accountCode, String cartCode) {

        Account account = getByAccountCode(accountCode);
        account.setCartCode(cartCode);

    }

    @Override
    @Transactional
    public void applyDepositCode(String accountCode, String depositCode) {

        Account account = getByAccountCode(accountCode);
        account.setDepositCode(depositCode);

    }


    @Override
    public Account create(CreateAccountRequest request) {

        Account account = Account.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        account = accountRepository.save(account);

        AccountCreatedEvent event = new AccountCreatedEvent(account.getCode());

        kafkaTemplate.send(accountsEventsTopicName, event);

        return account;

    }

    @Override
    @Transactional(readOnly = true)
    public Account getByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Account getByAccountCode(String accountCode) {
        return accountRepository.findByCode(accountCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
    }


    @Override
    @Transactional
    public void deleteByCode(String code) {
        Account findAccount = getByAccountCode(code);
        accountRepository.delete(findAccount);
    }


}
