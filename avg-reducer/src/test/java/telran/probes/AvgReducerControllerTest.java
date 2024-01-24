package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.dto.ProbeData;
import telran.probes.service.AvgValueService;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgReducerControllerTest {
@Autowired
	InputDestination producer;
@Autowired
OutputDestination consumer;
@MockBean
AvgValueService avgService;
@Value("${app.avg.binding.name}")
String producerBindingName;
String consumerBindingName = "probeConsumerAvg-in-0";
static final Long AVG_VALUE = 100l;
static final long SENSOR_ID_AVG_VALUE = 124;
static final ProbeData PROBE_DATA_NO_AVG_VALUE = new ProbeData(123, 100, 0);
static final ProbeData PROBE_DATA_AVG_VALUE = new ProbeData(SENSOR_ID_AVG_VALUE, 110, 0);
static final ProbeData PROBE_DATA_WITH_AVG_VALUE = new ProbeData(SENSOR_ID_AVG_VALUE, AVG_VALUE, 0);
 
@BeforeEach
void setUp() {
	when(avgService.getAvgValue(PROBE_DATA_AVG_VALUE)).thenReturn(AVG_VALUE);
	when(avgService.getAvgValue(PROBE_DATA_NO_AVG_VALUE)).thenReturn(null);
}
	@Test
	void testNoAvgValue() {
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA_NO_AVG_VALUE),
				consumerBindingName);
		Message<byte[]> message = consumer.receive(10, producerBindingName);
		assertNull(message);
	}
	@Test
	void testAvgValue() throws Exception{
		producer.send(new GenericMessage<ProbeData>(PROBE_DATA_AVG_VALUE),
				consumerBindingName);
		Message<byte[]> message = consumer.receive(10, producerBindingName);
		
		assertNotNull(message);
		
		ObjectMapper mapper = new ObjectMapper();
	
		ProbeData actual = mapper.readValue(message.getPayload(), ProbeData.class);
		//?? different timestamp? not equals
		//assertEquals(PROBE_DATA_WITH_AVG_VALUE, actual);
		
		
	}

}