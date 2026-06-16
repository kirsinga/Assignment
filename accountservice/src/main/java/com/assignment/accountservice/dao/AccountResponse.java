package com.assignment.accountservice.dao;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {

    private Status status;

    private String message;

    private Instant timestamp;

    private ErrorResponse error;

    private AccountDao account;

    private BalanceDao balance;

    private List<AccountDao> accountHistory;

    public static AccountResponse success(
            String message,
            AccountDao account) {

        return AccountResponse.builder()
                .status(Status.SUCCESS)
                .message(message)
                .timestamp(Instant.now())
                .account(account)
                .build();
    }

    public static AccountResponse failure(
            String code,
            String message) {

        return AccountResponse.builder()
                .status(Status.FAILED)
                .timestamp(Instant.now())
                .error(new ErrorResponse(code, message, null))
                .build();
    }

    public enum Status {
        SUCCESS,
        FAILED
    }
}