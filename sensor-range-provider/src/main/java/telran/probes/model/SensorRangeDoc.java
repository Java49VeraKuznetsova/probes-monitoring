package telran.probes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection="sensor-ranges")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SensorRangeDoc {
	@Id
	long sensorId;
	float minValue;
	float maxValue;
}
