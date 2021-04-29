// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.service;

public class UftpParticipantServiceStub implements UftpParticipantService {
    public UftpDomain getUftpDomainDetails(String domain) {
        switch (domain) {
            case "xxx.com" : return new UftpDomain("http://localhost:8081/api/messages", "1C497ABD6A2DF6942E6A0D58BB8CA0F36C6B5A428277F409C9BFE4BF20E6A43B");
            case "yyy.com" : return new UftpDomain("http://localhost:8002/api/messages", "F45C0A99D3764BC532C025DDB40D291F5F28D9535063C00F80C65E104A08B8F6");
        }
        return null;
    }
}
