# Shapeshifter Library

Java library for implementing [Shapeshifter](https://www.lfenergy.org/projects/shapeshifter/) using Spring Boot.

## Getting Started
Add the Shapeshifter Connector dependency to your Spring Boot application:
```xml
<dependency>
  <groupId>org.lfenergy.shapeshifter</groupId>
  <artifactId>shapeshifter-connector</artifactId>
  <version>${shapeshifter.version}</version>
</dependency>
```


### Receiving UFTP messages

Adding the Connector dependency adds an endpoint to your application, where you can receive UFTP messages.
Typically this endpoint is at: http://localhost:8080/USEF/2019/SignedMessage.

Add a bean to your Spring Boot application to handle incoming UFTP messages:
```java
@UftpIncomingHandler
public class IncomingMessageHandler {

  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant from, FlexRequest flexRequest) {
    // ...
  }
}
```
You can add multiple methods for different types of messages. For every Flex message type there is a corresponding `Mapping` annotation.

To process a message (asynchronously) you can use the `UftpReceivedMessageService`:
```java
@Autowired 
UftpReceivedMessageService uftpReceivedMessageService;

uftpReceivedMessageService.process(sender, message);
```

To unseal an incoming message, the connector must know the sender's public key. For this you must provide a `UftpParticipantService` bean in your application:
```java
@Service
public class MyUftpParticipantService implements UftpParticipantService {
  // ...
}
```

Incoming messages are automatically validated. For this you must provide a `UftpValidatorSupport` bean in your application:
```java
@Service
public class MyUftpValidatorSupport implements UftpValidatorSupport {
  // ...
}
```

### Sending UFTP messages

Use the `UftpSendMessageService` bean to send UFTP messages to recipients:
```java
@Autowired 
UftpSendMessageService uftpSendMessageService;

var message = new FlexRequest();

var sender = new UftpParticipant("sender.com", USEFRoleType.DSO);
var recipient = new UftpParticipant("recipient.com", USEFRoleType.AGR);
var shippingDetails = new ShippingDetails(sender, "myPrivateKey", recipient);

uftpSendMessageService.attemptToSendMessage(message, shippingDetails);
```

## Build and test
```
mvn install
```

## Custom validations
Add one or more beans to your Spring Boot application that implement `UftpUserDefinedValidator`:
```java
@Service
public class MyCustomValidator implements UftpUserDefinedValidator<FlexRequest> {

  @Override
  public boolean appliesTo(Class<? extends FlexRequest> clazz) {
    return clazz.equals(FlexRequest.class);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexRequest flexRequest) {
    // ...
  }

  @Override
  public String getReason() {
    return "My custom validation failed";
  }
}
```
Any custom validations are picked up and called automatically after the standard validations.

