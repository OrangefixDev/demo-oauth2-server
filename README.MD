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





******** below is how the documents look in mongodb ************

account:
{"_id":"aaron","_class":"com.gary.demo.oauth2.entity.Account","password":"$2a$10$GlwUMZRpab77BzJvZrOLaORKJ8tXRNj58yafMT7j9garDEu3MntsG","firstName":"aaron","lastName":"wang","email":"aaron.wang@email.com","roles":["ROLE_USER","ROLE_ADMIN","ROLE_EVERYTHING"],"accountNonExpired":true,"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true}

client:
{"_id":"democrud","_class":"com.gary.demo.oauth2.entity.Client","resourceIds":"democrud","clientSecret":"123password","scope":"read,write","authorizedGrantTypes":"refresh_token,client_credentials,password","registeredRedirectUri":"","authorities":"com.gary.demo.oauth2.entity.Client$1@537b32ef","accessTokenValiditySeconds":7200,"refreshTokenValiditySeconds":7200}