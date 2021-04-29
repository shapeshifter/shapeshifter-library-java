// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(length=10485760)
    private String message;
    private String senderDomain;
    private String recipientDomain;
    private boolean incomming;
    private boolean outgoing;
    private Long retryCount;
    private boolean successfullSend;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    

    protected Message() {}

    public Message(String message, String senderDomain, String recipientDomain, boolean incomming, boolean outgoing, Long retryCount, boolean successfullSend) {
        this.message = message;
        this.senderDomain = senderDomain;
        this.recipientDomain = recipientDomain;
        this.incomming = incomming;
        this.outgoing = outgoing;
        this.retryCount = retryCount;
        this.successfullSend = successfullSend;
        this.created = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderDomain() {
        return senderDomain;
    }

    public String getRecipientDomain() {
        return recipientDomain;
    }

    public boolean getIncomming() {
        return incomming;
    }

    public boolean getOutgoing() {
        return outgoing;
    }

    public Long getRetryCount() {
        return retryCount;
    }

    public boolean getSuccessfullSend() {
        return successfullSend;
    }

}
