package telran.probes.configuration;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class SensorsConfiguration {
	@Value("${app.sensors.binding.name:sensorsData-out-0}")
	String bindingName;
	
	@SuppressWarnings("serial")
	HashMap<Long, SensorData> sensorsDataMap = new HashMap<>() {
		{
			put(123l, new SensorData(100, 200, new String[] { "service123@gmail.com" }));
			put(124l, new SensorData(-10, 20, new String[] { "service124@gmail.com" }));
			put(125l, new SensorData(10, 40, new String[] { "service125@gmail.com" }));
		}
	};
	@Value("${app.sensors.deviation.percent: 10}")
	int deviationPercent;
	@Value("${app.sensors.deviation.factor: 0.3}")
	float deviationFactor;
	@Value("${app.sensors.negative.deviation.percent: 50}")
	int negativeDeviationPercent;
}
