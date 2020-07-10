package de.opengamebackend.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class GatewayApplication {
	public static void main(String[] args) {
		// https://www.baeldung.com/zuul-load-balancing
		SpringApplication.run(GatewayApplication.class, args);
	}
}
