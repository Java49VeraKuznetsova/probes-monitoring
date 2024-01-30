package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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

import lombok.extern.slf4j.Slf4j;
import telran.probes.service.EmailDataProviderClient;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@Slf4j
class EmailNotificationServiceTest {
@Autowired
InputDestination producer;
String consumerBindingName = "configChangeConsumer-in-0";
@MockBean
RestTemplate restTemplate;
@Autowired
EmailDataProviderClient providerService;
static final long SENSOR_ID = 123l;
static final long SENSOR_ID_NOT_FOUND = 124l;
static final long SENSOR_ID_UNAVALAIBLE = 125l;
private static final String [] EMAILS = {
		"name1@gmail.com",
		"name2@telran.co.il"
	};
private static final String [] EMAILS_UPDATE = {
		"name3@gmail.com",
		"name4@gmail.com"
			};
private static final String URL = "http://localhost:8282/sensor/email/";
@Value ("${app.update.token.email:email-update}")
String emailUpdateToken;
@Value("${app.update.message.delimiter:#}")
String delimeter;
@Value("${app.sensor.email.provider.default.email:name@gmail.com}")
String []defaultEmail;

	@Test
	@Order(1)
	void test() {
		assertNotNull(producer);
		assertNotNull(restTemplate);
	}
	@Test
	@Order(2)
	void normalFlowWithNoMapData() {
		log.debug("Test 2");
		mockRemoteServiceRequest(EMAILS, HttpStatus.OK, SENSOR_ID);
		String[] actual = providerService.getEmails(SENSOR_ID);
		assertEquals(EMAILS, actual);
		
	}
	@Test
	@Order(3)
	void normalFlowWithMapData() {
		log.debug("Test 3");
		testNoServiceCalled();
		String[] actual = providerService.getEmails(SENSOR_ID);
		assertEquals(EMAILS, actual);
	}
	@Test
	@Order (4)
	void sensorNotFound() {
		log.debug("Test 4");
		ResponseEntity<String> responseEntityNotFound =
				new ResponseEntity<>(String.format("Sensor with id %d not found", 
						SENSOR_ID_NOT_FOUND), HttpStatus.NOT_FOUND);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), 
				any(), any(Class.class))).thenReturn(responseEntityNotFound);
		String[] actual = providerService.getEmails(SENSOR_ID_NOT_FOUND);
	
		assertArrayEquals(defaultEmail, actual);
		
		ResponseEntity<String[]> responseEntity = 
				new ResponseEntity<String[]>(EMAILS, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), 
						any(), any(Class.class))).thenReturn(responseEntity);	
		actual = providerService.getEmails(SENSOR_ID_NOT_FOUND);
		assertEquals(EMAILS, actual);
		
	}
	@Test
	@Order (5)
	void remoteServiceIsUnavailable() {
		//Test case for default sensor
		log.debug("Test 5");
		
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenThrow(new RuntimeException("Service is unavalaible"));
		String[] actual = providerService.getEmails(SENSOR_ID_UNAVALAIBLE);
		final String[] EMAILS_DEFAULT = defaultEmail;
		assertArrayEquals(EMAILS_DEFAULT, actual);
		
		//Test case for default sensor doesn't exist in the map
		ResponseEntity<String[]> responseEntity = 
				new ResponseEntity(EMAILS, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(),
				any(Class.class))).thenReturn(responseEntity);
		mockRemoteServiceRequest(EMAILS, HttpStatus.OK, SENSOR_ID_UNAVALAIBLE);
		actual = providerService.getEmails(SENSOR_ID_UNAVALAIBLE);
		assertEquals(EMAILS, actual);
	}
	@Test
	@Order (6)
	void sensorInMapUpdated() {
		log.debug("Test 6");
		//Test for update map with new sensor
		mockRemoteServiceRequest(EMAILS_UPDATE, HttpStatus.OK, SENSOR_ID);
		producer.send(new GenericMessage<String>(String.format("%s%s%d", 
				emailUpdateToken, delimeter, SENSOR_ID)));
		String[] actual = providerService.getEmails(SENSOR_ID);
		//!!!!!!!!!!
		assertEquals(EMAILS_UPDATE, actual);
	
	}
	@Test
	@Order (7)
	void sensorNotInMapUpdated() {
		// no sensor in map - send a call
		log.debug("Test 7");
		testNoServiceCalled();
		producer.send(new GenericMessage<String>(String.format("%s%s%d", 
				emailUpdateToken, delimeter, 100000)),consumerBindingName);
	}
	@Test
	@Order (8)
	void emailUpdate() {
		//change SensorRange - not to change map
		log.debug("Test 8");
		testNoServiceCalled();
		producer.send(new GenericMessage<String>(String.format("%s%s%d",
				"sensorRange", delimeter, SENSOR_ID)));
	}
	private void mockRemoteServiceRequest(String[] emails, HttpStatus status, long sensorId) {
		ResponseEntity<String[]> responseEntity = 
				new ResponseEntity<String[]>(emails, status);
				when(restTemplate.exchange(URL + sensorId, HttpMethod.GET, null,String[].class))
				.thenReturn(responseEntity);
				
		
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
