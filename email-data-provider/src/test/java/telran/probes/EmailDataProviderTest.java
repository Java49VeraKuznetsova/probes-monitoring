package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.probes.model.SensorEmailsDoc;
import telran.probes.repo.SensorEmailsRepo;


@SpringBootTest
@AutoConfigureMockMvc
class EmailDataProviderTest {

@Autowired
MockMvc mockMvc;
@Autowired
SensorEmailsRepo sensorEmailsRepo;
@Value("${app.emails.provider.url}")
String url;
static final long SENSOR_ID = 123;

private static final String EMAIL1 = "service123@gmail";
SensorEmailsDoc sensorEmailsDoc = new SensorEmailsDoc(SENSOR_ID, new String[]{EMAIL1});
String[]emailsExpected = {EMAIL1};
@Autowired
ObjectMapper mapper;
static final long SENSOR_NOT_FOUND_ID = 10000;
static final String ERROR_MESSAGE = "sensor " + SENSOR_NOT_FOUND_ID + " not found";
@BeforeEach
void setUp() {
	sensorEmailsRepo.save(sensorEmailsDoc);
}
	@Test
	void normalFlowTest() throws  Exception {
		String fullUrl = "http://localhost:8080" + url + "/" + SENSOR_ID;
		
		String response = mockMvc.perform(get(fullUrl))
		.andDo(print()).andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();
		String[] emailsActual = mapper.readValue(response, String[].class);
		assertArrayEquals(emailsExpected, emailsActual);
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