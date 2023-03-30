// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

class XmlToolsTest {

  @Test
  void getOuterTagNameWithoutNamespace() {
    assertThat(XmlTools.getOuterTagNameWithoutNamespace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><foo xmlns=\"bar\">")).isEqualTo("foo");
    assertThat(XmlTools.getOuterTagNameWithoutNamespace("<foo xmlns=\"bar\">")).isEqualTo("foo");
    assertThat(XmlTools.getOuterTagNameWithoutNamespace("<foo>")).isEqualTo("foo");
    assertThat(XmlTools.getOuterTagNameWithoutNamespace("<foo xmlns=\"bar\" lorem=\"ipsum\">bar</foo>baz<foo>baz</foo>")).isEqualTo("foo");
    assertThat(XmlTools.getOuterTagNameWithoutNamespace("<foo xmlns=\"bar\">bar</foo>baz<foo>baz</foo>baz")).isEqualTo("foo");
  }

  @Test
  void getOuterTagNameWithoutNamespace_error() {
    var actual = assertThrows(UftpConnectorException.class, () -> XmlTools.getOuterTagNameWithoutNamespace("foo"));

    assertThat(actual.getMessage()).isEqualTo("No tags found in XML.");
  }
}