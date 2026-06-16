package com.assignment.eventservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.assignment.eventservice.dao.EventDao;
import com.assignment.eventservice.dao.EventResponseDao;

@FeignClient(
        name = "account-service"
)
public interface AccountServiceClient {

    @PostMapping(
            value = "/accounts/{accountId}/transaction",
            consumes = "application/json",
            produces = "application/json"
    )
    ResponseEntity<EventResponseDao> postAccountTransaction(
            @RequestHeader("X-Method-Name") String methodName,
            @PathVariable("accountId") String accountId,
            @RequestBody EventDao eventDao
    );

    @GetMapping(
            value = "/accounts/{accountId}/history",
            produces = "application/json"
    )

    EventResponseDao getAccountHistory(
            @PathVariable("accountId") String accountId
    );
    @GetMapping(
            value = "/accounts/{accountId}/balance",
            produces = "application/json"
    )
    EventResponseDao getbanlence(
            @PathVariable("accountId") String accountId
    );

}