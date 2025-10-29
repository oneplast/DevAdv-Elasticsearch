package io.eddie.accountservice.mapper;


import io.eddie.accountservice.model.dto.AccountDescription;
import io.eddie.accountservice.model.dto.AccountDetail;
import io.eddie.accountservice.model.entity.Account;

public class AccountMapper {

    public static AccountDescription toDescription(Account account) {
        return new AccountDescription(account.getCode(), account.getUsername());
    }

    public static AccountDetail toDetail(Account account) {
        return new AccountDetail(account.getUsername(), account.getEmail());
    }

}
