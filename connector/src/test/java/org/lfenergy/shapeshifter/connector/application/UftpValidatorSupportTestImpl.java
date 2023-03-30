// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import java.time.Duration;
import java.util.TimeZone;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Component;

@Component
public class UftpValidatorSupportTestImpl implements UftpValidatorSupport {

  @Override
  public boolean isSupportedIspDuration(Duration duration) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSupportedTimeZone(TimeZone timeZone) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isValidBaselineReference(String baselineReference) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
