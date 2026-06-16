package com.in28minutes.microservices.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				
			
				.route("event-service", r -> r
	                    .path("/events/**")
	                    .uri("lb://event-service"))
				
				.route("account-service", r -> r
	                    .path("/account/**")
	                    .uri("lb://account-service"))
				.build();
	}

}
