package telran.probes;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.model.ProbeDataDoc;
import telran.probes.repo.ProbeDataRepo;
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class AvgPopulatorAppl {
final ProbeDataRepo probeDataRepo;
	public static void main(String[] args) {
		SpringApplication.run(AvgPopulatorAppl.class, args);

	}
	@Bean
	Consumer<ProbeData> avgPopulatorConsumer() {
		return this::probeDataPopulation;
	}
	void probeDataPopulation(ProbeData probeData) {
		log.debug("received probeData: {}", probeData);
		ProbeDataDoc probeDataDoc = probeDataRepo.save(new ProbeDataDoc(probeData));
		log.debug("Document {} has been saved to Database", probeDataDoc);
	}

}
