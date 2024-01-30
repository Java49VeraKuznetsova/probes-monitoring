package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import telran.probes.dto.ProbeDataDeviation;
import telran.probes.service.EmailDataProviderClient;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class EmailNotifierControllerTest {
	private static final long SENSOR_ID = 123;
	@Autowired
InputDestination producer;
	@MockBean
	EmailDataProviderClient providerClient;
	@RegisterExtension
	static GreenMailExtension mailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
	.withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "12345.com"));
	ProbeDataDeviation deviationData = new ProbeDataDeviation(SENSOR_ID, 100, 10, 0);
	String[] emails = {
		"name1@gmail.com",
		"name2@telran.co.il"
	};
	private String consumerBindingName = "deviationConsumer-in-0";
	@Test
	void test() throws MessagingException {
		when(providerClient.getEmails(SENSOR_ID)).thenReturn(emails);
		producer.send(new GenericMessage<ProbeDataDeviation>(deviationData), consumerBindingName );
		MimeMessage[] messages = mailExtension.getReceivedMessages();
		assertTrue(messages.length > 0);
		MimeMessage message = messages[0];
		Address[] recipients = message.getAllRecipients();
		assertEquals(2, recipients.length);
		String[] actualEmails = Arrays.stream(recipients).map(Address::toString)
				.toArray(String[]::new);
		assertArrayEquals(emails, actualEmails);
		
	}

}