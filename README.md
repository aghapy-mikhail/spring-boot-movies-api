# Movies Discovery API #


### Table of Contents ###

* Overview
* OpenAPI SPecification
* Functional Requirements
* Technologies
* Run Locally
* Run using Docker
* TESTING
    + Unit Tests 
    + Integration Tests
    + Load tests
* Keycloak    
* Jenkins
* Kubernetes







### Overview ###

This is a restful API that'll help sorting out movies and add them to users' favorites list based on their genres or names.



### OpenAPI SPecification ###

* [SwaggerHub](https://app.swaggerhub.com/apis/cakin/movies-sys-api/1.0.0#/) 



### Functional Requirements ###

* Get a list of actors that is filterable based on actors' names.
* Create,read, update and delete an `actor` .
* Get a list of movies that should be filterable based on their `genres` or `movieName` 
* Create,read, update and delete a `movie`. The system allows to add or delete a genre from a movie record using `genreId`.
* Get a list of users,genres and favorite lists. Favorites list uses pagination so we can change the default values of `limit` and `page`.
* The system allows to update a `favorite` list by adding or erasing movies from it's `movies` list.
* Create,read, update and delete an `user` or `genre` record .




### Technologies ###

* Java 11
* MySQL
* Maven
* JUnit
* JMeter
* Postman
* Docker
* Keycloak
* Jenkins


### Run Locally ###
Ports
* API-8080
* MySQL-3306


### Run Using Docker###

You can run this project using docker containers. Make sure that Docker is installed and go to Movies/ folder where you can run the `docker-compose up -d` command and start the necessary containers. It should take a while to start all containers since movies-api container waits for keycloak. 

Make sure that the necessary ports are available  `3306` for mysql database ,`8090` for movies-api and `8080` for keycloak container.



#### Clone-Repo ####
`git clone https://cakinn@bitbucket.org/mountainstatesoftware/cakin-sb-proj.git`

### TESTING ###

* Unit Tests
* Integration Tests
* Load Tests

#### Unit tests ####
Unit tests for this project were made using JUnit and Mockito. Run tests and generate the report using `mvn clean test`
The report can be found at ` target\site\jacoco\index.html` .


#### Integration tests ####
Integration tests were made using Postman. Install node to run from the command line or run the collection using Postman UI. Navigate to the postman folder and run the following command iterating the tests 5 times.

`>newman run -n 5 movies-api-remake.postman_collection.json -e movies-local.postman_environment.json`


#### Load tests ####
Load tests were made using Apache JMeter. The tests should hit 1000 requests to each endpoint of the API. Try to reach that number increasing the loop number (100 users and 10 loops). 
The report can be found at `test-report\index.html`.

`jmeter -n -t movies-api-x.jmx -l load-test.csv -e -o test-report`



### Keycloak ###

In this project Keycloak is used for authentication and every single request must have a JWT(Jason Web Token) present in it's header.

To acces that token wait tillmovies-auth container is up and running and send a POST request using the values below:

`
client_id: movies-api
username: apiuser
password: password
grant_type: password
client_secret: rxb5qnx4JnCmcZQ0vNwh2mNKIYT2ZNU4

`

Keycloak realm data is present in keycloak/movies-realm.json , a JSON file which was exported from a keycloak container with all of it's credentials, users and clients.


### Jenkins ###

This project uses a Jenkins script for executing a pipeline and run tests. It takes about 6-8 min to run all tests.

Make sure that Jenkins is installed and that the necessary credentials ares set, navigate where the jar file is located and run :
`java -jar jenkins.war --httpPort=9090` 

Run Jenkins on port 9090 to avoid collading with other containers.

Then set a pipeline and set URL for this repository, choose the branch and specify the script path.


### Kubernetes ###
In 'Kubernetes' folder can be found files that deploy all components to a kubernetes cluster using `movies` namespace.
