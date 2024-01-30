package telran.probes;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeDataDeviation;
import telran.probes.service.EmailDataProviderClient;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class EmailNotifierAppl {
final EmailDataProviderClient providerClient;
final JavaMailSender mailSender;
	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}
	@Bean
	Consumer<ProbeDataDeviation> deviationConsumer() {
		return this::deviationNotificationSend;
	}
	void deviationNotificationSend(ProbeDataDeviation deviationData) {
		log.debug("received deviation {}", deviationData);
		long sensorId = deviationData.sensorId();
		String[] emails = providerClient.getEmails(sensorId);
		sendMails(emails, deviationData);
	}
	private void sendMails(String[] emails, ProbeDataDeviation deviationData) {
		String text = getText(deviationData);
		SimpleMailMessage smm = new SimpleMailMessage();
		String subject = getSubject(deviationData);
		smm.setTo(emails);
		smm.setText(text);
		smm.setSubject(subject);
		mailSender.send(smm);
		log.debug("text: {}, subject: {}", text);
		log.debug("emails : {}", Arrays.deepToString(emails));
		log.debug("mail sent to above emails");
		
	}
	private String getSubject(ProbeDataDeviation deviationData) {
		return String.format("Deviation of sensor %d", deviationData.sensorId());
	}
	private String getText(ProbeDataDeviation deviationData) {
		
		return String.format("Sensor %d has value %f \n deviation is %f",
				deviationData.sensorId(), deviationData.value(), deviationData.deviation());
	}

}