package telran.probes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
@Configuration
@Getter
public class EmailsProviderConfiguration {
	@Value("${app.emails.provider.host:localhost}")
	String host;
	@Value("${app.emails.provider.port:8484}")
	int port;
	@Value("${app.emails.provider.url:/emails}")
	String url;
	@Value("${app.emails.provider.default:sensors.service@gmail.com}")
	String [] emails;
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}