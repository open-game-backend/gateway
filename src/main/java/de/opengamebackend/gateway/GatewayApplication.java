package de.opengamebackend.gateway;

import de.opengamebackend.util.EnableOpenGameBackendUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableZuulProxy
@EnableOpenGameBackendUtils
public class GatewayApplication {
	public static void main(String[] args) {
		// https://www.baeldung.com/zuul-load-balancing
		SpringApplication.run(GatewayApplication.class, args);
	}
}
