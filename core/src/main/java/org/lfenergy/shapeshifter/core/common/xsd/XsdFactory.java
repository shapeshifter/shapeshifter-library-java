// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

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
}
