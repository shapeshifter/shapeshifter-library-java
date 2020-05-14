package eu.uftplib.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.uftplib.entity.Message;
import eu.uftplib.repository.MessageRepository;
import eu.uftplib.service.MessageDirection;
import eu.uftplib.service.UftpParticipantService;
import eu.uftplib.service.UftpService;
import eu.uftplib.service.UftpSigningService;
import eu.uftplib.service.UftpValidationService;

@RestController
public class UftpController {
	private UftpService uftpService;
	private MessageRepository messageRepository;
	private UftpValidationService uftpValidationService;
	private UftpSigningService uftpSigningService;
	private UftpParticipantService uftpParticipantService;

	public UftpController(UftpService uftpService, MessageRepository messageRepository, UftpValidationService uftpValidationService, UftpSigningService uftpSigningService, UftpParticipantService uftpParticipantService) {
		this.uftpService = uftpService;
		this.messageRepository = messageRepository;
		this.uftpValidationService = uftpValidationService;
		this.uftpSigningService = uftpSigningService;
		this.uftpParticipantService = uftpParticipantService;
	}

	@RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uftp(HttpEntity<String> httpEntity) {
		String xml = httpEntity.getBody();
		System.out.println(xml);
		var signedMessageDomainPair = uftpValidationService.validateXml(xml, MessageDirection.Incomming);
		if (signedMessageDomainPair == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		var domain = uftpParticipantService.getUftpDomainDetails(signedMessageDomainPair.getSenderDomain());
		var message = uftpSigningService.unsealMessage(xml, domain.getPublicKey());
		var messageDomainPair = uftpValidationService.validateXml(message, MessageDirection.Incomming);
		if (messageDomainPair == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        var m = messageRepository.save(new Message(message, messageDomainPair.getSenderDomain(), messageDomainPair.getRecipientDomain(), true, false, 0L, false));
		uftpService.notifyNewMessage(m.getId(), message);
		return ResponseEntity.ok(null);
	}
}