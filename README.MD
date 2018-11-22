***to run the application***


1. create a file called application.properties with the following content:

spring.data.mongodb.host=
spring.data.mongodb.port=
spring.data.mongodb.database=
spring.data.mongodb.username=
spring.data.mongodb.password=

2. start the app with the following command:
java -jar -Dapp.config.path=<path to the properties file> demo-oauth2-1.0-SNAPSHOT.jar


example: 
java -jar -Dapp.config.path=c:\demo demo-oauth2-1.0-SNAPSHOT.jar