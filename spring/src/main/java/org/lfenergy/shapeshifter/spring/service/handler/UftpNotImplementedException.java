package org.lfenergy.shapeshifter.spring.service.handler;

import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

public class UftpNotImplementedException extends UftpConnectorException {

    public UftpNotImplementedException(String message) {
        super(message);
    }

}
