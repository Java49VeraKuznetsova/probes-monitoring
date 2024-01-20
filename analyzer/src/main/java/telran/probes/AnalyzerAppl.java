package telran.probes;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;
import telran.probes.service.SensorRangeProviderService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AnalyzerAppl {
final SensorRangeProviderService providerService;
final StreamBridge streamBridge;
@Value("${app.deviation.binding.name:deviation-out-0}")
String deviationBindingName;
	public static void main(String[] args) {
		SpringApplication.run(AnalyzerAppl.class, args);

	}
	@Bean
	public Consumer<ProbeData> consumerProbeData() {
		return this::consumeMethod;
	}
	void consumeMethod(ProbeData probeData) {
		log.trace("received probe: {}", probeData);
		long sensorId = probeData.sensorId();
		SensorRange range = providerService.getSensorRange(sensorId);
		float value = probeData.value();
		
		int border = 0;
		if (value < range.minValue()) {
			border = range.minValue();
		} else if(value > range.maxValue()) {
			border = range.maxValue();
		}
		if (border != 0) {
			float deviation = value - border;
			log.debug("deviation: {}", deviation);
			ProbeDataDeviation dataDeviation =
					new ProbeDataDeviation(sensorId, value, deviation, System.currentTimeMillis());
			streamBridge.send(deviationBindingName, dataDeviation);
			log.debug("deviation data {} sent to {}", dataDeviation, deviationBindingName);
			
		}
	}
	

}
