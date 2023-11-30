# Migration
This document registers all steps that need to be taken in order to upgrade from one version of the Shapeshifter Java library to another. Most updates to the Shapeshifter Java library will be backwards compatible, and require no additional steps from the library user, but sometimes a breaking change is applied, which requires action from the library user.

# Version 2.0
When upgrading to Shapeshifter Java library version 2.x from 1.x, you need to consider the following changes:

There are several breaking changes in the class `UftpMessageSupport`; these changes were necessary to fix a bug in which a `FlexRequestResponse` was sent that referred to an existing `FlexRequest`, but had a different conversation ID. Thus, the main driver for these changes was to include the conversation ID in several validations. 

### `UftpMessage`
The method `referenceToPreviousMessage` now includes the `conversationId`.

### `UftpMessageReference`
Constructor now includes the `conversationId`.

### `UftpMessageSupport`
All methods in `UftpMessageSupport` have been renamed, some methods have one or more extra parameters (like `conversationID` or `senderDomain`)

| Previous                                                               | New                                                        | Description                                                                                                                                                                                                                                                                                                                                                                                                        |
|------------------------------------------------------------------------|------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `getPreviousMessage(String messageID, String recipientDomain)`         | `getDuplicateMessage()`                                    | The method `getPreviousMessage()` has been renamed to `getDuplicateMessage()`, and a parameter `senderDomain` was added, to check whether a message is duplicate in the communication between both the sender domain and the recipient domain.                                                                                                                                                                     |
| `getPreviousMessage(UftpMessageReference<T> reference)`                | `findReferencedMessage(UftpMessageReference<T> reference)` | This method has been renamed to `findReferencedMessage(UftpMessageReference<T> reference)`                                                                                                                                                                                                                                                                                                                         |
| `isValidOrderReference(String orderReference, String recipientDomain)` | N/A                                                        | This method has been removed, since the same can be achieved by `findReferencedMessage(UftpMessageReference<T> reference)`                                                                                                                                                                                                                                                                                         |
| `existFlexRevocation()`                                                | `findFlexRevocation()`                                     | The method `existsFlexRevocation(String flexOfferMessageId, String recipientDomain)` has been renamed to `findFlexRevocation(String conversationID, String flexOfferMessageID, String senderDomain, String recipientDomain);`; the `flexOfferMessageID` and the `senderDomain` were added as two additional parameters, and the method now returns the actual Flex Offer revocation (if any) instead of a Boolean. |


## Non-breaking changes
### `FlexOfferRevocationSenderDomainValidator`
This validator class is new, it validates that a flex offer revocation originates from the same `senderDomain` as the original flex offer.

