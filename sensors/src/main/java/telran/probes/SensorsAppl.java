package telran.probes;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.configuration.SensorsConfiguration;
import telran.probes.dto.ProbeData;
import telran.probes.service.SensorsService;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor

public class SensorsAppl {
	public static final long TIMEOUT = 10000;
	final SensorsService sensorService;
	final StreamBridge streamBridge;
	final SensorsConfiguration sensorsConfiguration;
	
	
	
public static void main(String[] args) throws InterruptedException {
	ConfigurableApplicationContext ctx = SpringApplication.run(SensorsAppl.class, args);
	Thread.sleep(TIMEOUT);
	ctx.close();
}
@Bean
Supplier<ProbeData> sensorsData() {
	return this::getRandomProbeData;
}
ProbeData getRandomProbeData() {
	String bindingName = sensorsConfiguration.getBindingName();
	 ProbeData probeData = sensorService.getRandomProbeData();
	 log.debug("probe data: {} has been sent to {}", probeData, bindingName);
	 return probeData;
}




}
