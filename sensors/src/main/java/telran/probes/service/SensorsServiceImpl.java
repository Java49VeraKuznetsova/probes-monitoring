package telran.probes.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.configuration.SensorData;
import telran.probes.configuration.SensorsConfiguration;
import telran.probes.dto.ProbeData;
import telran.probes.dto.SensorRange;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorsServiceImpl implements SensorsService{
	final SensorsConfiguration sensorsConfiguration;
	@Override
	public ProbeData getRandomProbeData() {
		Map<Long, SensorData> sensorsMap = sensorsConfiguration.getSensorsDataMap();
		long[] sensorIds = sensorsMap.keySet().stream().mapToLong(id -> id).toArray();
		long id = getRandomId(sensorIds);
		SensorData sensorData = sensorsMap.get(id);
		SensorRange range = new SensorRange(sensorData.minValue(), sensorData.maxValue());
		
		return new ProbeData(id, getRandomInt(1, 100) < sensorsConfiguration.getDeviationPercent() ? 
				getRandomDeviation(range) : getRandomNormalValue(range), System.currentTimeMillis());
	}
	private long getRandomId(long[] sensorIds) {
		int index = getRandomInt(0, sensorIds.length);
		return sensorIds[index];
	}
	private float getRandomNormalValue(SensorRange range) {
		
		return ThreadLocalRandom.current().nextFloat(range.minValue(), range.maxValue());
	}
	private float getRandomDeviation(SensorRange range) {
		
		return getRandomInt(1,100) < sensorsConfiguration.getNegativeDeviationPercent() ?
				getLessMin(range.minValue()) : getGreaterMax(range.maxValue());
	}
	private float getGreaterMax(float maxValue) {
		
		float res =  maxValue + Math.abs(maxValue * sensorsConfiguration.getDeviationFactor());
		log.debug("positive deviation - maxValue: {}, new value: {}", maxValue, res);
		return res;
	}
	private float getLessMin(float minValue) {
		
		float res =  minValue - Math.abs(minValue * sensorsConfiguration.getDeviationFactor());
		log.debug("negative deviation - minValue: {}, new value: {}", minValue, res);
		return res;
	}
	private int getRandomInt(int min, int max) {
		
		return ThreadLocalRandom.current().nextInt(min, max);
	}

}
