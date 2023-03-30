// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.xsdinfo;

import java.net.URL;

public enum UftpXsds {

  COMMON(UftpXsds.class.getResource("/UFTP-common.xsd")),
  AGR(UftpXsds.class.getResource("/UFTP-agr.xsd")),
  AGR_CRO(UftpXsds.class.getResource("/UFTP-agr-cro.xsd")),
  AGR_DSO(UftpXsds.class.getResource("/UFTP-agr-dso.xsd")),
  CRO(UftpXsds.class.getResource("/UFTP-cro.xsd")),
  CRO_DSO(UftpXsds.class.getResource("/UFTP-cro-dso.xsd")),
  DSO(UftpXsds.class.getResource("/UFTP-dso.xsd")),
  METERING(UftpXsds.class.getResource("/UFTP-metering.xsd")),
  ALL(UftpXsds.class.getResource("/UFTP.xsd"));

  private final URL url;

  UftpXsds(URL url) {
    this.url = url;
  }

  public URL getUrl() {
    return url;
  }
}
