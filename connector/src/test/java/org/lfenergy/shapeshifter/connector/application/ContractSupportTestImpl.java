// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import org.lfenergy.shapeshifter.connector.service.validation.ContractSupport;
import org.springframework.stereotype.Component;

@Component
public class ContractSupportTestImpl implements ContractSupport {

  @Override
  public boolean isSupportedContractID(String contractId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
