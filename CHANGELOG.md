# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 3.7.0
### Added
- `UftpValidator#validate` returns a `ValidationResult`, so a validator can return a rejection reason with details. The default implementation uses `isValid` and `getReason`, so existing validators keep working.
- `ValidationResult.of(valid, rejectionReason)` creates an ok or rejection result in one call. The reason is only used when `valid` is false.

### Changed
- `UftpValidator#isValid` and `UftpValidator#getReason` are deprecated; implement `UftpValidator#validate` instead. They are planned for removal in the next major release.
- Built-in validators now implement `UftpValidator#validate` directly and return their rejection reason in the `ValidationResult`.

### Migration guide
Replace `isValid` and `getReason` with one `validate` method that returns both the validity and rejection reason:

```java
@Override
public ValidationResult validate(UftpMessage<MyMessage> message) {
  if (isMessageValid(message)) {
    return ValidationResult.ok();
  }
  return ValidationResult.rejection("My validation failed");
}
```

Return `ValidationResult.ok()` for a valid message. For an invalid message, return `ValidationResult.rejection(reason)` with a non-blank reason.

## 3.6.0
### Fixed
- Fixed the EAN length in the Metering message (#221).

## 3.5.0
### Changed
- #214 Trim attribute values

## 3.4.0
### Changed
- fix: #153 Invalid status code: 524

## 3.3.0
### Changed
- Upgrade to Spring Boot 4

## 3.2.2
### Changed
- Remove default from "Unsolicited" attribute in XSD to be backwards compatible with V3.0.0

## 3.2.1
### Changed
- Fix XSDs for V3.1.0

## 3.2.0

### Changed
- Updated to Shapeshifter specification version 3.1.0

## 3.1.0

### Changed
- Update Spring Boot version to 3.4.1

## 3.0.0

### Added

- Support for authorization when sending UFTP messages. This entails:  
  - the addition of the ParticipantAuthorizationProvider interface that is used to take care of the authorization
  - a stub implementation of ParticipantAuthorizationProvider in the spring-module, which throws an exception when called
  - the extension of UftpParticipantInformation with a property that tells whether authorization is required
  - see README.md for more details


