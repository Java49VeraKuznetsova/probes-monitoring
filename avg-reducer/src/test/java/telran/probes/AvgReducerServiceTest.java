package telran.probes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import telran.probes.dto.ProbeData;
import telran.probes.model.ProbesList;
import telran.probes.repo.ProbesListRepo;
import telran.probes.service.AvgValueService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.*;

@SpringBootTest
public class AvgReducerServiceTest {
	static List<Float> VALUES_NO_AVG;
	static List<Float> VALUES_AVG;
	static final long SENSOR_ID_NO_REDIS_RECORD = 123l;
	static final long SENSOR_ID_NO_AVG = 124l;
	static final long SENSOR_ID_AVG = 125l;
	static final float VALUE = 100f;
	static final ProbeData PROBE_NO_REDIS_RECORD = new ProbeData(SENSOR_ID_NO_REDIS_RECORD, VALUE, 0);
	static final ProbeData PROBE_NO_AVG = new ProbeData(SENSOR_ID_NO_AVG, VALUE, 0);
	static final ProbeData PROBE_AVG = new ProbeData(SENSOR_ID_AVG, VALUE, 0);
	static final ProbesList PROBES_LIST_NO_AVG = new ProbesList(SENSOR_ID_NO_AVG);
	static final ProbesList PROBES_LIST_AVG = new ProbesList(SENSOR_ID_AVG);
	static final ProbesList PROBES_LIST_NO_RECORD = new ProbesList(SENSOR_ID_NO_REDIS_RECORD);
	static final Map<Long, ProbesList> mapRedis = new HashMap<>();
	@Autowired
	AvgValueService avgValueService;	
	@MockBean
	ProbesListRepo probesListRepo;
	@BeforeEach
	void setUp() {
		VALUES_NO_AVG = PROBES_LIST_NO_AVG.getValues();
		VALUES_AVG = PROBES_LIST_AVG.getValues();
		VALUES_AVG.add(VALUE);
		mapRedis.put(SENSOR_ID_NO_AVG, PROBES_LIST_NO_AVG);
		mapRedis.put(SENSOR_ID_AVG, PROBES_LIST_AVG);
	}
	@Test
	void testNoRedisRecord() {
		when(probesListRepo.findById(SENSOR_ID_NO_REDIS_RECORD)).thenReturn(Optional.ofNullable(null));
		when(probesListRepo.save(PROBES_LIST_NO_RECORD)).thenAnswer(new Answer<ProbesList>() {

			@Override
			public ProbesList answer(InvocationOnMock invocation) throws Throwable {
				mapRedis.put(SENSOR_ID_NO_REDIS_RECORD, invocation.getArgument(0));
				return invocation.getArgument(0);
			}
		});
		Long res = avgValueService.getAvgValue(PROBE_NO_REDIS_RECORD);
		assertNull(res);
		ProbesList probesList = mapRedis.get(SENSOR_ID_NO_REDIS_RECORD);
		assertNotNull(probesList);
		assertEquals(VALUE, probesList.getValues().get(0));
	}
	@Test
	void testNoAvgValue() {
		//TODO
	}
	@Test
	void testAvgValue() {
		//TODO
	}
}
