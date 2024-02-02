package telran.probes.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.exceptions.NotFoundException;
import telran.probes.model.SensorEmailsDoc;
import telran.probes.repo.SensorEmailsRepo;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDataProviderServiceImpl implements EmailDataProviderService {
	final SensorEmailsRepo sensorEmailsRepo;
	@Override
	public String[] getEmails(long sensorId) {
		SensorEmailsDoc emailsDoc = sensorEmailsRepo.findById(sensorId)
				.orElseThrow(() -> new NotFoundException
						(String.format("sensor %d not found", sensorId)));
		log.debug("sensor {} has been found in DB", sensorId);
		return emailsDoc.getEmails();
	}

}
