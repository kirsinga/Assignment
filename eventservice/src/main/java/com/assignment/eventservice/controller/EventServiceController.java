package com.assignment.eventservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.assignment.eventservice.dao.EventDao;
import com.assignment.eventservice.dao.EventResponseDao;
import com.assignment.eventservice.service.EventService;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
@Validated
public class EventServiceController {

    private final EventService eventService;

    // ✅ Constructor Injection (Best Practice)
    public EventServiceController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * POST event transaction
     */
    @PostMapping
    @CircuitBreaker(name = "eventService", fallbackMethod = "postEventFallback")
    public ResponseEntity<EventResponseDao> postEvent(@Valid @RequestBody EventDao eventDao) {

        ResponseEntity<EventResponseDao> response = eventService.postEventInfo(eventDao);
        return response;
    }

    /**
     * GET account info
     */
    @GetMapping("/account")
    @RateLimiter(name = "default")
    public EventResponseDao getAccountHistory(@RequestParam String accountId) {

        EventResponseDao result = eventService.getAccountHistory(accountId);
        return result;
    }

    /**
     * GET event history
     */
    @GetMapping("/{eventId}")
    @Retry(name = "post",fallbackMethod = "postEventFallback")
    public ResponseEntity<EventDao> getEventHistory(@PathVariable("eventId") String eventId) {

        EventDao result = eventService.getEventInfo(eventId);
        return ResponseEntity.ok(result);
    }
    
    public ResponseEntity<EventResponseDao> postEventFallback(
            EventDao eventDao,
            Throwable ex) {

        EventResponseDao fallbackResponse = new EventResponseDao();
        fallbackResponse.setStatus("FAILED");
        fallbackResponse.setMessage("Service temporarily unavailable. Please try again later.");

        return ResponseEntity
                .status(503)
                .body(fallbackResponse);
    }
    /**
     * GET account balance
     */
    @GetMapping("/{accountId}/balence")
    @Bulkhead(name = "default")
    public ResponseEntity<EventResponseDao> getAccountBalence(@PathVariable("accountId") String accountId) {

        EventResponseDao eventResponseDao = eventService.getAccountBalence(accountId);
        return ResponseEntity.ok(eventResponseDao);
    }
    
}
   
   