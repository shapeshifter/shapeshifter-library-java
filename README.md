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
