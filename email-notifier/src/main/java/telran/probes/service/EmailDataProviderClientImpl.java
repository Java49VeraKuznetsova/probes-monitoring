package telran.probes.service;

import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDataProviderClientImpl implements EmailDataProviderClient {
@Getter
HashMap<Long, String[]> mapEmails = new HashMap<>();
@Value ("${app.update.token.email:email-update}")
String emailUpdateToken;
@Value("${app.update.message.delimiter:#}")
String delimeter;
final RestTemplate restTemplate;
final EmailDataProviderConfiguration providerConfiguration;

	@Override
	public String[] getEmails(long sensorId) {
		String[] emails = mapEmails.get(sensorId);
		
		return emails == null ? getEmailsFromService(sensorId) : emails;
	}
	Consumer<String> configConsumer() {
		return this::checkConfigurationUpdate;
	}
void checkConfigurationUpdate(String message) {
	
	String [] tokens = message.split(delimeter);
	if(tokens[0].equals(emailUpdateToken)) {
		updateMapEmails(tokens[1]);
	}
}
	private void updateMapEmails(String sensorIdStr) {
	long id = Long.parseLong(sensorIdStr);
	if (mapEmails.containsKey(id)) {
		mapEmails.put(id, getEmailsFromService(id));
	}
	
}
	private String[] getEmailsFromService(long id) {
		String[] res = null;
		try {
		ResponseEntity<?> responseEntity =
				restTemplate.exchange(getFullUrl(id), HttpMethod.GET, null, String[].class);
		if(!responseEntity.getStatusCode().is2xxSuccessful()) {
			throw new Exception((String) responseEntity.getBody());
		}
		res = (String[])responseEntity.getBody();
		mapEmails.put(id, res);
	} catch (Exception e) {
		log.error("no sensor email provided for sensor {}, reason {}", id, e.getMessage());
		res = getDefaultEmail();
		//!!!
		log.warn("Taken default email {}");
		
	}
		log.debug("Emails for sensor {} is {}", id, res);
		return res;
	}
	private String[] getDefaultEmail() {
		String[] defaultMails = providerConfiguration.getDefaultEmail();
		return defaultMails;
	}
	private String getFullUrl(long id) {
		String res = String.format("http://%s:%d%s/%d",
				providerConfiguration.getHost(),
				providerConfiguration.getPort(),
				providerConfiguration.getUrl(),
				id);
		log.debug("url:{}", res);
		return res;
	}

}
