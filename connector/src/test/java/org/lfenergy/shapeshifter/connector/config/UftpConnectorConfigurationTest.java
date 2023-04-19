package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.connector.application.UftpControllerTestApp;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.receiving.UftpReceivedMessageService;
import org.lfenergy.shapeshifter.connector.service.sending.UftpSendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = UftpControllerTestApp.class)
class UftpConnectorConfigurationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void loadsCryptoService_autoConfigurationEnabled() {
    var cryptoService = applicationContext.getBean(UftpCryptoService.class);
    assertThat(cryptoService).isNotNull();
  }

  @Test
  void loadsUftpReceivedMessageService_autoConfigurationEnabled() {
    var receivedMessageService = applicationContext.getBean(UftpReceivedMessageService.class);
    assertThat(receivedMessageService).isNotNull();
  }

  @Test
  void loadsUftpSendMessageService_autoConfigurationEnabled() {
    var receivedMessageService = applicationContext.getBean(UftpSendMessageService.class);
    assertThat(receivedMessageService).isNotNull();
  }

}