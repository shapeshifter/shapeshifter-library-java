// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

public class XmlTools {

  private static final String OPTIONAL_NAMESPACE = "(\\w*:)?";
  private static final String TAG_NAME = "(\\w*)?";
  private static final String START_TAG_REGEX = "<" + OPTIONAL_NAMESPACE + TAG_NAME + "\\b";

  private static final Pattern START_TAG_PATTERN = Pattern.compile(START_TAG_REGEX);

  public static String getOuterTagNameWithoutNamespace(final String xml) {
    final Matcher matcher = START_TAG_PATTERN.matcher(xml);
    if (matcher.find()) {
      return matcher.group(2);
    }
    throw new UftpConnectorException("No tags found in XML.");
  }
}
