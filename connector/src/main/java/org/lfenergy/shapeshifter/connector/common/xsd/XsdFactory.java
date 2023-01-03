package org.lfenergy.shapeshifter.connector.common.xsd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
@RequiredArgsConstructor
public class XsdFactory {

  private final XsdSchemaFactoryPool factoryPool;

  public XsdSchemaPool newXsdSchemaPool(final URL url) {
    return new XsdSchemaPool(url, factoryPool);
  }

  public String contentToString(File file) {
    try {
      return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    } catch (final IOException cause) {
      throw new UftpConnectorException("Failed to read file content from '" + file + "'.", cause);
    }
  }

  public String contentToString(final InputStream inputStream) {
    try {
      return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    } catch (final IOException cause) {
      throw new UftpConnectorException("Failed to read stream content.", cause);
    }
  }
}
