package telran.probes.service;

import telran.probes.dto.SensorRange;

public interface SensorRangeProviderService {
SensorRange getSensorRange(long sensorId);
}
