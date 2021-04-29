// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.service;

public class KeyPair {
    private String PublicKey;
    private String PrivateKey;

    public KeyPair(String publicKey, String privateKey) {
        this.PublicKey = publicKey;
        this.PrivateKey = privateKey;
    }

    public String getPublicKey() {
        return this.PublicKey;
    }

    public String getPrivateKey() {
        return this.PrivateKey;
    }
}