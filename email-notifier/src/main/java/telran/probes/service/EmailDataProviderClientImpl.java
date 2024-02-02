package telran.probes.service;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.configuration.EmailsProviderConfiguration;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDataProviderClientImpl implements EmailDataProviderClient {
	@Getter
	HashMap<Long, String[]> mapEmails = new HashMap<>();
	@Value("${app.update.message.delimiter:#}")
	String delimiter;
	@Value("${app.update.token.emails:emails-update}")
	String emailsUpdateToken;
	final EmailsProviderConfiguration providerConfiguration;
	final RestTemplate restTemplate;
	@Override
	public String[] getEmails(long sensorId) {
		String[] emails = mapEmails.get(sensorId);
		return emails == null ? getEmailsFromRemoteService(sensorId) : emails;
	}
	private String[] getEmailsFromRemoteService(long sensorId) {
		String[] res =null;
		try {
			ResponseEntity<?> responseEntity = 
			restTemplate.exchange(getFullUrl(sensorId), HttpMethod.GET, null, String[].class);
			if(!responseEntity.getStatusCode().is2xxSuccessful()) {
				throw new Exception((String) responseEntity.getBody());
			}
			res = (String[])responseEntity.getBody();
			mapEmails.put(sensorId, res);
			log.debug("emails for sensor {} are {}", sensorId, Arrays.deepToString(res));
		} catch (Exception e) {
			log.error("no email address provided for sensor {}, reason: {}",
					sensorId, e.getMessage());
			res = getDefaultEmails();
			log.warn("Taken default emails {}", Arrays.deepToString(res));
		}
		
		return res;
	}
	private String[] getDefaultEmails() {
		
		return providerConfiguration.getEmails();
	}
	private String getFullUrl(long sensorId) {
		String res = String.format("http://%s:%d%s/%d",
				providerConfiguration.getHost(),
				providerConfiguration.getPort(),
				providerConfiguration.getUrl(),
				sensorId);
		log.debug("url:{}", res);
		return res;
	}
	@Bean
	Consumer<String> configChangeConsumer() {
		return this::checkConfigurationUpdate;
	}
void checkConfigurationUpdate(String message) {
		
		String [] tokens = message.split(delimiter);
		if(tokens[0].equals(emailsUpdateToken)) {
			updateMapEmails(tokens[1]);
		}
	}
private void updateMapEmails(String sensorIdStr) {
	long id = Long.parseLong(sensorIdStr);
	if (mapEmails.containsKey(id)) {
		getEmailsFromRemoteService(id);
	}
	
}

}
