package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.model.SensorEmailsDoc;

public interface SensorEmailsRepo extends MongoRepository<SensorEmailsDoc, Long> {

}
