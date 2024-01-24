package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.messaging.support.GenericMessage;

import telran.probes.dto.ProbeData;
import telran.probes.model.ProbeDataDoc;
import telran.probes.repo.ProbeDataRepo;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgPopulatorTest {
	private static final long SENSOR_ID = 123;
	private static final float VALUE = 100;
	@MockBean
	MongoTransactionManager transactionManager;
	@Autowired
	InputDestination producer;
	static final ProbeData probeData = new ProbeData(SENSOR_ID, VALUE, 
			System.currentTimeMillis());
	@Autowired
	ProbeDataRepo probeDataRepo;
	String consumerBindingName = "avgPopulatorConsumer-in-0";
	@Test
	@DisplayName("Populating proper ProbeDataDoc object to MongoDB")
	void test() {
		producer.send(new GenericMessage<ProbeData>(probeData), consumerBindingName);
		List<ProbeDataDoc> docs = probeDataRepo.findAll();
		assertEquals(1, docs.size());
		ProbeDataDoc probeDataDoc = docs.get(0);
		assertEquals(SENSOR_ID, probeDataDoc.getSensorID());
		assertEquals(VALUE, probeDataDoc.getValue());
	}

}
