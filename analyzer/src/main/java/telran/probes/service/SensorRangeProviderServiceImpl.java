package telran.probes.service;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.SensorRange;
@Service
@RequiredArgsConstructor
@Slf4j
public class SensorRangeProviderServiceImpl implements SensorRangeProviderService {
	@Getter
	HashMap<Long, SensorRange> mapRanges = new HashMap<>();
	@Value("${app.update.message.delimiter:#}")
	String delimiter;
	@Value("${app.update.token.range:range-update}")
	String rangeUpdateToken;
	final SensorRangeProviderConfiguration providerConfiguration;
	final RestTemplate restTemplate;
	@Override
	public SensorRange getSensorRange(long sensorId) {
		SensorRange range = mapRanges.get(sensorId);
		
		return range == null ? getRangeFromService(sensorId) : range;
	}
	@Bean
	Consumer<String> configChangeConsumer() {
		return this::checkConfigurationUpdate;
	}
	void checkConfigurationUpdate(String message) {
		
		String [] tokens = message.split(delimiter);
		if(tokens[0].equals(rangeUpdateToken)) {
			updateMapRanges(tokens[1]);
		}
	}
	private void updateMapRanges(String sensorIdStr) {
		long id = Long.parseLong(sensorIdStr);
		if (mapRanges.containsKey(id)) {
			mapRanges.put(id, getRangeFromService(id));
		}
		
	}
	private SensorRange getRangeFromService(long id) {
		SensorRange res =null;
		try {
			ResponseEntity<SensorRange> responseEntity = 
			restTemplate.exchange(getFullUrl(id), HttpMethod.GET, null, SensorRange.class);
			res = responseEntity.getBody();
			mapRanges.put(id, res);
		} catch (Exception e) {
			log.error("no sensor range provided for sensor {}, reason: {}",
					id, e.getMessage());
			res = getDefaultRange();
			log.warn("Taken default range {} - {}", res.minValue(), res.maxValue());
		}
		log.debug("Range for sensor {} is {}", id, res);
		return res;
	}
	private SensorRange getDefaultRange() {
		
		return new SensorRange(providerConfiguration.getMinDefaultValue(),
				providerConfiguration.getMinDefaultValue());
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
