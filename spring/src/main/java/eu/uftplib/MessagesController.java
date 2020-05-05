package eu.uftplib;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class MessagesController {

	@RequestMapping(value = "/api/messages", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String messages(HttpEntity<String> httpEntity) {
		String xml = httpEntity.getBody();
		System.out.println(xml);
		return "OK";
	}

}