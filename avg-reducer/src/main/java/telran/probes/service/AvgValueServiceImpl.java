package telran.probes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.model.ProbesList;
import telran.probes.repo.ProbesListRepo;
import java.util.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class AvgValueServiceImpl implements AvgValueService {
final ProbesListRepo probesListRepo;
@Value("${app.average.reducing.size}")
int reducingSize;
	@Override
	@Transactional
	public Long getAvgValue(ProbeData probeData) {
		long sensorId = probeData.sensorId();
		Long res = null;
		ProbesList probesList = probesListRepo.findById(sensorId).orElse(null);
		if (probesList == null) {
			probesList = new ProbesList(sensorId);
			log.debug("probesList for sensor {} doesn't exist", sensorId);
		}
		List<Float> values = probesList.getValues();
		values.add(probeData.value());
		if(values.size() == reducingSize) {
			log.debug("reducing for sensor {}",sensorId);
			res = (long) values.stream().mapToLong(v -> v.longValue())
					.average().orElse(0);
			values.clear();
		}
		probesListRepo.save(probesList);
		log.debug("saved probesList {}", probesList);

		return res;
	}

}