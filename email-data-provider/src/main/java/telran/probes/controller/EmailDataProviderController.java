package telran.probes.controller;
import java.util.Arrays;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.service.EmailDataProviderService;
@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailDataProviderController {
final EmailDataProviderService providerService;
@GetMapping("${app.emails.provider.url}" + "/{id}")
String[] getEmails(@PathVariable(name="id") long id) {
	String[] emails =  providerService.getEmails(id);
	log.debug("emails received are {}", Arrays.deepToString(emails));
	return emails;
}
}