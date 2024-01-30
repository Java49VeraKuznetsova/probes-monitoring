package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.dto.SensorRange;
import telran.probes.model.SensorRangeDoc;
import telran.probes.repo.SensorRangesRepo;
import telran.probes.service.*;


@SpringBootTest
@AutoConfigureMockMvc
class SensorRangeProviderTest {
@Autowired
SensorRangeProviderService service;
@Autowired
MockMvc mockMvc;
@Autowired
SensorRangesRepo sensorRangesRepo;
@Value("${app.sensor.range.provider.url}")
String url;
static final long SENSOR_ID = 123;
private static final float MIN_VALUE = 10;
private static final float MAX_VALUE = 100;
SensorRangeDoc sensorRangeDoc = new SensorRangeDoc(SENSOR_ID, MIN_VALUE, MAX_VALUE);
SensorRange sensorRangeExpected = new SensorRange(MIN_VALUE, MAX_VALUE);
@Autowired
ObjectMapper mapper;
static final long SENSOR_NOT_FOUND_ID = 10000;
static final String ERROR_MESSAGE = "sensor " + SENSOR_NOT_FOUND_ID + " not found";
@BeforeEach
void setUp() {
	sensorRangesRepo.save(sensorRangeDoc);
}
	@Test
	void normalFlowTest() throws  Exception {
		String fullUrl = "http://localhost:8080" + url + "/" + SENSOR_ID;
		
		String response = mockMvc.perform(get(fullUrl))
		.andDo(print()).andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		SensorRange actual = mapper.readValue(response, SensorRange.class);
		assertEquals(sensorRangeExpected, actual);
	}
	@Test
	void notFoundFlowTest() throws  Exception {
		String fullUrl = "http://localhost:8080" + url + "/" + SENSOR_NOT_FOUND_ID;
		
		String response = mockMvc.perform(get(fullUrl))
		.andDo(print()).andExpect(status().isNotFound())
		.andReturn().getResponse().getContentAsString();
		
		assertEquals( ERROR_MESSAGE, response);
	}

}