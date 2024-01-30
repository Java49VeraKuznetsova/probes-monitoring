package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.model.SensorRangeDoc;

public interface SensorRangesRepo extends MongoRepository<SensorRangeDoc, Long> {

}
