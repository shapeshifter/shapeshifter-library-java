package org.lfenergy.shapeshifter.connector.service.sending;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UftpSendFactory {

  public RestTemplate newRestTemplate() {
    return new RestTemplate();
  }
}
