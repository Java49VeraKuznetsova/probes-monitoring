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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.SensorRange;
import telran.probes.service.SensorRangeProviderService;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("unchecked")
class AnalyzerServiceTest {
	@Autowired
InputDestination producer;
	String consumerBindingName = "configChangeConsumer-in-0";
	@MockBean
	RestTemplate restTemplate;
	@Autowired
	SensorRangeProviderService providerService;
	static final long SENSOR_ID = 123l;
	static final long SENSOR_ID_NOT_FOUND = 124l;
	private static final float MIN_VALUE = 10;
	private static final float MAX_VALUE = 20;
	static final SensorRange SENSOR_RANGE = new SensorRange(MIN_VALUE, MAX_VALUE);
	private static final long SENSOR_ID_UNAVAILABLE = 125l;
	private static final SensorRange SENSOR_RANGE_UPDATED = new SensorRange(MIN_VALUE + 10,
			MAX_VALUE + 10);
	private static final String URL = "http://localhost:8282/sensor/range/";
	@Value("${app.sensor.range.provider.default.min}")
	float minDefaultValue;
	@Value("${app.sensor.range.provider.default.max}")
	float maxDefaultValue;
	@Value("${app.update.message.delimiter}")
	String delimiter;
	@Value("${app.update.token.range}")
	String rangeUpdateToken;
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
		testNoServiceCalled();
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE, actual);
		
	}
	@Test
	@Order(3)
	void sensorNotFound() {
		//Test case for default sensor range
		ResponseEntity<String> responseEntityNotFound =
				new ResponseEntity<>(String.format("Sensor with id %d not found",
						SENSOR_ID_NOT_FOUND), HttpStatus.NOT_FOUND);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenReturn(responseEntityNotFound);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID_NOT_FOUND);
		final SensorRange SENSOR_RANGE_DEFAULT = new SensorRange(minDefaultValue,
				maxDefaultValue);
		assertEquals(SENSOR_RANGE_DEFAULT, actual);
		//Test case for default sensor range doesn't exist in the map
		ResponseEntity<SensorRange> responseEntity =
				new ResponseEntity<SensorRange>(SENSOR_RANGE, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenReturn(responseEntity);
		actual = providerService.getSensorRange(SENSOR_ID_NOT_FOUND);
		assertEquals(SENSOR_RANGE, actual);
	}
	@Test
	@Order(4)
	void remoteServiceIsUnavailable() {
		//Test case for default sensor range
		
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenThrow(new RuntimeException("Service is unavailable"));
		SensorRange actual = providerService.getSensorRange(SENSOR_ID_UNAVAILABLE);
		final SensorRange SENSOR_RANGE_DEFAULT = new SensorRange(minDefaultValue,
				maxDefaultValue);
		assertEquals(SENSOR_RANGE_DEFAULT, actual);
		//Test case for default sensor range doesn't exist in the map
		ResponseEntity<SensorRange> responseEntity =
				new ResponseEntity<SensorRange>(SENSOR_RANGE, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenReturn(responseEntity);
		mockRemoteServideRequest(SENSOR_RANGE, HttpStatus.OK, SENSOR_ID_UNAVAILABLE);
		actual = providerService.getSensorRange(SENSOR_ID_UNAVAILABLE);
		assertEquals(SENSOR_RANGE, actual);
	}
	@Test
	@Order(5)
	void sensorInMapUpdated() {
		mockRemoteServideRequest(SENSOR_RANGE_UPDATED, HttpStatus.OK, SENSOR_ID);
		producer.send(new GenericMessage<String>(String.format("%s%s%d",
				rangeUpdateToken, delimiter, SENSOR_ID)), consumerBindingName);
		SensorRange actual = providerService.getSensorRange(SENSOR_ID);
		assertEquals(SENSOR_RANGE_UPDATED, actual);
	}
	@Test
	@Order(6)
	void sensorNotInMapUpdated() {
		testNoServiceCalled();
		producer.send(new GenericMessage<String>(String.format("%s%s%d",
				rangeUpdateToken, delimiter, 100000)), consumerBindingName);
		
	}
	@Test
	@Order(7)
	void emailUpdate() {
		testNoServiceCalled();
		producer.send(new GenericMessage<String>(String.format("%s%s%d",
				"email", delimiter, SENSOR_ID)), consumerBindingName);
		
	}
	private void mockRemoteServideRequest(SensorRange sensorRange, HttpStatus status, 
			long sensorId) {
		ResponseEntity<SensorRange> responseEntity =
				new ResponseEntity<SensorRange>(sensorRange, status);
		when(restTemplate.exchange(URL + sensorId,
				HttpMethod.GET, null,
				SensorRange.class)).thenReturn(responseEntity);
	}
	
	
	private void testNoServiceCalled() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenAnswer(new Answer<ResponseEntity<?>>() {
					@Override
					public ResponseEntity<?> answer(InvocationOnMock invocation) throws Throwable {
						fail("service shouldn't be called");
						return null;
					}
				});
	}

}
