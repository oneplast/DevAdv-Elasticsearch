package io.eddie.accountservice.mapper;


import io.eddie.accountservice.model.dto.AuthenticationDetails;
import io.eddie.accountservice.model.entity.Account;

public class AuthenticationMapper {

    public static AuthenticationDetails toDetails(Account account) {
        return new AuthenticationDetails(account.getCode(), account.getUsername(), account.getPassword(), account.getRoles());
    }

}
