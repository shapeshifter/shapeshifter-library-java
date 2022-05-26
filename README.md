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

# Regenerate the model classes

Java code can be generated form the xsd using JAXB xjc tool.  This is included with java 8 jre, but not from java 9 onwards.

To regenerate the model classes follow these steps:
1. Copy the new xsd file into /src/main/resources
2. Run the generation for the three top level xsds:
/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-agr.xsd
/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-dso.xsd
/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc UFTP-cro.xsd
3. Copy the generated files into /src/main/java/generated

#########################
# Key encoding

The library uses code signing keys only, because the https is used for encrpytion.
Keys are encoded with base64 encoding per the uftp standard.
