// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import java.util.Collection;
import org.lfenergy.shapeshifter.api.EntityAddress;
import org.lfenergy.shapeshifter.connector.service.validation.CongestionPointSupport;
import org.springframework.stereotype.Component;

@Component
public class CongestionPointSupportTestImpl implements CongestionPointSupport {

  @Override
  public boolean areKnownCongestionPoints(Collection<EntityAddress> connectionPoints) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
