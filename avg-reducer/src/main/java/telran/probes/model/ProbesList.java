package telran.probes.model;

import java.util.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.*;

@RedisHash
@Getter

@ToString
@NoArgsConstructor
public class ProbesList {
	@Id
	long sensorId;
	List<Float> values;
	public ProbesList(long sensorId) {
		this.sensorId = sensorId;
		values = new ArrayList<>();
	}
	@Override
	public int hashCode() {
		return Objects.hash(sensorId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProbesList other = (ProbesList) obj;
		return sensorId == other.sensorId;
	}
	
	
}