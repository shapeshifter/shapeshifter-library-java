package eu.uftplib.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.uftplib.service.UftpValidationService;
import eu.uftplib.service.UftpValidationServiceImplementation;

@RestController
public class UftpController {
    @Value("${uftplib.role}")
	private String role;

	@RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String uftp(HttpEntity<String> httpEntity) {
		String xml = httpEntity.getBody();
		System.out.println(xml);
		UftpValidationService uftpValidationService = new UftpValidationServiceImplementation(role);
		if (!uftpValidationService.validateXml(xml)) return "ERROR";
		return "OK";
	}
}