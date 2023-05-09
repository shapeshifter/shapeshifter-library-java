// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

public enum UftpMessageDirection {

  INCOMING,
  OUTGOING;

  public UftpMessageDirection inverse() {
    return switch (this) {
      case INCOMING -> OUTGOING;
      case OUTGOING -> INCOMING;
    };
  }
}
