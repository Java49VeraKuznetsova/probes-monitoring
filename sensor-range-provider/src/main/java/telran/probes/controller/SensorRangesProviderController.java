package telran.probes.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.SensorRange;
import telran.probes.service.SensorRangeProviderService;
@RestController
@RequiredArgsConstructor
@Slf4j
public class SensorRangesProviderController {
	final SensorRangeProviderService service;
	@GetMapping("${app.sensor.range.provider.url}" + "/{id}")
	SensorRange getSensorRange(@PathVariable(name="id") long id) {
		SensorRange sensorRange =  service.getSensorRange(id);
		log.debug("sensor range received is {}", sensorRange);
		return sensorRange;
	}
	
	
}