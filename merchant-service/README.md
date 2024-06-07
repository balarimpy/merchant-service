# merchant-service
Merchant Service API

# Setting up your local
1. In the project root, copy `example.env` and paste it as a new file called `.env`.
2. In your new `.env` file, fill in a db username and password of choice.
3. Run the docker compose file with the following command `docker-compose up -d`
4. Run MerchantApplication, either through your IDE or with gradle (`./gradlew bootRun`).

# Service architecture
### config
A place to add all the configurations
### data
The data layer contains the Entity(Document and its Relationships) and Repository. The data layer should only by accessed by the service layer.  
### service
Service package will contain the business logic for all the methods. Usually services should be called by the controller. The service should not access the database directly, It should communicate with the data layer to get the data. 
Service layers should only throw MerchantServiceException and the exceptions should be logged before the service throws it.
### api
Api will contain all the RestController logic. api should invoke service methods to allow CRUD operations. The service methods will either return an Entity or a DTO. These should NOT be returned to the client as a response. Rather a response should be created separately in the response package and an appropriate response should be returned to the client along with the correct Response status.