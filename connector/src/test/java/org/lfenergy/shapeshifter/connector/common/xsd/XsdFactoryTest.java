package org.lfenergy.shapeshifter.connector.common.xsd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertExceptionCauseNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.common.xml.XmlFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XsdFactoryTest {

  @Mock
  private XsdSchemaFactoryPool factoryPool;
  @Mock
  private XmlFactory xmlFactory;

  @InjectMocks
  private XsdFactory testSubject;

  @Mock
  private IOException ioException;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(xmlFactory, factoryPool, ioException);
  }

  @Test
  public void newXsdSchemaPool() throws Exception {
    var url = new URL("file:///test.xsd");
    final XsdSchemaPool v1 = testSubject.newXsdSchemaPool(url);
    final XsdSchemaPool v2 = testSubject.newXsdSchemaPool(url);

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isNotSameAs(v1);

    assertThat(v1.getXsd()).isEqualTo(url);
    assertThat(v1.getFactoryPool()).isSameAs(factoryPool);
  }

  @Test
  void fileContentToString() {
    final String content = testSubject.contentToString(
        new File("src/test/resources/input.txt")
    );
    assertThat(content).isEqualTo("BOE");
  }

  @Test
  void fileContentToString_throws() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.contentToString(new File("not exist.file")));

    assertExceptionCauseNotNull(actual, "Failed to read file content from 'not exist.file'.");
  }

  @Test
  void streamContentToString() {
    final String content = testSubject.contentToString(
        new ByteArrayInputStream("BOE".getBytes(StandardCharsets.UTF_8))
    );
    assertThat(content).isEqualTo("BOE");
  }

  @Test
  void streamContentToString_throws() throws IOException {
    var inputStream = mock(InputStream.class);
    doThrow(new IOException("test")).when(inputStream).read(any(byte[].class), anyInt(), anyInt());

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.contentToString(inputStream));

    assertExceptionCauseNotNull(actual, "Failed to read stream content.");
  }

}
