package eu.uftplib.entity;

import java.util.Date;

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
    private String message;
    private String domain;
    private boolean incomming;
    private boolean outgoing;
    private Long retryCount;
    private boolean successfullSend;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    

    protected Message() {}

    public Message(String message, String domain, boolean incomming, boolean outgoing, Long retryCount, boolean successfullSend) {
        this.message = message;
        this.domain = domain;
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

    public String getDomain() {
        return domain;
    }

    public Long getRetryCount() {
        return retryCount;
    }

}
