// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.service;

public interface UftpValidationService {
    DomainPair validateXml(String xml, MessageDirection messageDirection);
}
