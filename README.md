🚀 Microservices Architecture (Event System)

This project is built using Spring Boot Microservices architecture with Spring Cloud API Gateway, Service Discovery, Centralized Config Server, and Resilience patterns (Resilience4j).

🧩 System Architecture Flow
Client
  ↓
API Gateway (Spring Cloud Gateway)
  ↓
Event Service
  ↓
Account Service
🌐 Base URLs
🔹 API Gateway
http://localhost:8765
🔹 Event Service
http://localhost:8082
🔹 Account Service
http://localhost:9000
🔹 Config Server (Centralized Config)
http://localhost:8888
🔹 Service Registry (Eureka Server)
http://localhost:8761
📌 API Endpoints (via API Gateway)
http://localhost:8765

All requests should be called through the API Gateway:

1️⃣ Create Event (with Account Transaction)

POST

/events

👉 Description:
Creates an event and posts account transaction details.

2️⃣ Get Account Transactions by Account ID

GET

/events/account?accountId=xxxx

👉 Description:
Fetches all transactions for a specific account.

3️⃣ Get Event by Event ID

GET

/events/{eventId}

👉 Description:
Returns event details for the given event ID.

4️⃣ Get Account Balance by Account ID

GET

/event/{accountId}/balance

👉 Description:
Fetches current balance of the account.

⚙️ Microservices Responsibilities
🔹 Event Service

Handles event creation and processing
Calls Account Service internally
Implements:
Circuit Breaker
Retry
Rate Limiting
Bulkhead (Resilience4j)


🔹 Account Service

Manages account data
Handles transaction updates
Provides balance and transaction APIs

🔹 API Gateway

Entry point for all requests
Handles routing to services

Provides:
Load balancing (for multiple instances)
Centralized routing
Service discovery integration

🔹 Config Server
Provides centralized configuration
Reads config from Git repository
Dynamically updates application properties

🔹 Service Registry (Eureka)

Registers all microservices
Enables dynamic service discovery
Supports load balancing between multiple instances

🛡️ Resilience Patterns Used

Implemented in Event Service:

Circuit Breaker → prevents cascading failure
Retry → retries failed requests automatically
Rate Limiter → controls request flow
Bulkhead → isolates system resources

⚖️ Load Balancing

API Gateway works with Eureka Service Registry to:

Discover multiple instances of services
Distribute requests automatically (load balancing)
Ensure high availability
🏗️ Tech Stack
Spring Boot
Spring Cloud Gateway
Spring Cloud Config Server
Spring Cloud Eureka
Resilience4j
REST APIs
Git-based Config Repository
📌 Summary

This system demonstrates a complete enterprise microservices architecture including:

API Gateway routing
Service discovery
Centralized configuration
Inter-service communication
Fault tolerance (Resilience4j)
Scalable load-balanced services












