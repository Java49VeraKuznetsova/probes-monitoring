package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.SensorRange;
import telran.probes.service.SensorRangeProviderService;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AnalyzerServiceTest {
	@Autowired
InputDestination producer;
	@MockBean
	RestTemplate restTemplate;
	@Autowired
	SensorRangeProviderService providerService;
	static final long SENSOR_ID = 123l;
	private static final float MIN_VALUE = 10;
	private static final float MAX_VALUE = 20;
	static final SensorRange SENSOR_RANGE = new SensorRange(MIN_VALUE, MAX_VALUE); 
	@SuppressWarnings("unchecked")
	@Test
	@Order(1)
	void normalFlowWithNoMapData() {
		ResponseEntity<SensorRange> responseEntity =
				new ResponseEntity<SensorRange>(SENSOR_RANGE, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenReturn(responseEntity);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE, actual);
		
	}
	@Test
	@Order(2)
	void normalFlowWithMapData() {
		
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE, actual);
		
	}

}