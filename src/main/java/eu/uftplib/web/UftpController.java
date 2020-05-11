package eu.uftplib.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.uftplib.entity.Message;
import eu.uftplib.repository.MessageRepository;
import eu.uftplib.service.UftpService;
import eu.uftplib.service.UftpValidationService;
import eu.uftplib.service.UftpValidationServiceImplementation;

@RestController
public class UftpController {
    @Value("${uftplib.role}")
	private String role;

	private UftpService uftpService;
	private MessageRepository messageRepository;

	public UftpController(UftpService uftpService, MessageRepository messageRepository) {
		this.uftpService = uftpService;
		this.messageRepository = messageRepository;
	}

	@RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String uftp(HttpEntity<String> httpEntity) {
		String xml = httpEntity.getBody();
		System.out.println(xml);
		//UftpValidationService uftpValidationService = new UftpValidationServiceImplementation(role);
		//if (!uftpValidationService.validateXml(xml)) return "ERROR";
        var m = messageRepository.save(new Message(xml, true, false, 0L, false));
		uftpService.notifyNewMessage(m.getId(), xml);
		return "OK";
	}
}