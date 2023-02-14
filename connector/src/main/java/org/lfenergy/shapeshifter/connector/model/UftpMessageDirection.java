package org.lfenergy.shapeshifter.connector.model;

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
