package com.assignment.accountservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.accountservice.dao.AccountDao;
import com.assignment.accountservice.dao.AccountResponse;
import com.assignment.accountservice.service.AccountService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/accounts")
@Validated
public class AccountServiceController {

    private  AccountService accountService;

    public AccountServiceController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Create Account Transaction
     */
    @PostMapping("/{accountId}/transaction")
    public ResponseEntity<AccountResponse> postAccountTransaction(
            @PathVariable
            @NotBlank(message = "Account Id cannot be empty")
            String accountId,

            @Valid
            @RequestBody
            AccountDao accountDetails) {

        // Always use accountId from URL
        accountDetails.setAccountId(accountId);

        AccountResponse response =
                accountService.postAccount(accountDetails);

        return ResponseEntity.ok(response);
    }

    /**
     * Get Current Balance
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountResponse> getAccountBalance(
            @PathVariable
            @NotBlank(message = "Account Id cannot be empty")
            String accountId) {

        AccountResponse response =
                accountService.getAccountBalance(accountId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get Account History
     */
    @GetMapping("/{accountId}/history")
    public ResponseEntity<AccountResponse> getAccountHistory(
            @PathVariable
            @NotBlank(message = "Account Id cannot be empty")
            String accountId) {

        AccountResponse response =
                accountService.getAccountHistory(accountId);

        return ResponseEntity.ok(response);
    }
}