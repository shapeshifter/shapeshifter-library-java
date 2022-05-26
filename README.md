# uftplib
UFTP library

To use the library the following settings must be set.

```
######################
# DATABASE SETTINGS  #
######################

## PostgreSQL
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.datasource.continue-on-error=true

#drop n create table again, good for testing, comment this in production
spring.jpa.hibernate.ddl-auto=none
spring.jpa.generate-ddl=true

uftplib.role=AGR/DSO/CRO
uftplib.privatekey=<privatekey to sign outgoingmessages>
```

!! Be sure to check the ParticipantServiceStub. This stub should return the correct endpoints en public keys for the remote uftp services

# Build the library

To build the library run the command:

on linux:
./gradlew build

on Windows:
gradlew.bat build

this will produce the library in the location:

uftplib/build/libs/eu.uftplib-0.0.1-SNAPSHOT.jar

# Test the library

To test the library run the command:

on linux:
./gradlew test

on Windows:
gradlew.bat test

#########################
# Key encoding

The library uses code signing keys only, because the https is used for encrpytion.
Keys are encoded with base64 encoding per the uftp standard.
