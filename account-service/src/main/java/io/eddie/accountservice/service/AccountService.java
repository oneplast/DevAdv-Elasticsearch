package io.eddie.accountservice.service;


import io.eddie.accountservice.model.dto.CreateAccountRequest;
import io.eddie.accountservice.model.entity.Account;

public interface AccountService {

    void applyCartCode(String accountCode, String cartCode);
    void applyDepositCode(String accountCode, String depositCode);

    Account create(CreateAccountRequest request);
    Account getByUsername(String username);
    Account getByAccountCode(String accountCode);

    void deleteByCode(String code);
}
