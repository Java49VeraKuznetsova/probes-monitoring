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
import telran.probes.service.AvgValueService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AvgReducerAppl {
	final AvgValueService service;
	final StreamBridge streamBridge;
	@Value("${app.avg.binding.name}")
	String bindingName;
	public static void main(String[] args) {
		SpringApplication.run(AvgReducerAppl.class, args);

	}
	@Bean
	Consumer<ProbeData> pulseProbeConsumerAvg() {
		return this::processPulseProbe;
	}
	void processPulseProbe(ProbeData probe) {
		log.trace("{}", probe);
		long sensorId = probe.sensorId();
		Long avgValue = service.getAvgValue(probe);
		if (avgValue != null) {
			log.debug("for patient {} avg value is {}", sensorId, avgValue);
			streamBridge.send(bindingName, new ProbeData(sensorId, avgValue,System.currentTimeMillis()));
		} else {
			log.trace("for patient {} no avg value yet", sensorId);
		}
		
		
	}
	
}
