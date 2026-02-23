# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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


