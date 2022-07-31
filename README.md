![Status](https://github.com/iundarigun/my-bank-challenge/actions/workflows/my-bank-challenge-ci.yml/badge.svg)
# My Bank challenge

Welcome to the my-bank challenge! This repository contains the code to run an application to manage customers, banks, accounts and transfers from/to database.
 
## How to run

We need MockWS and Postgres to run. I prepared two ways:

1. MockWS and Postgres images are getting from docker hub, but my bank challenge is compiling when starts:
```shell
docker-compose -f environment/docker-compose-compiling.yml up
```
_Advice_: This option can fail on first execution, because `depends on` instruction doesn't wait for ready connections on database, and application sometimes is ready before database, mainly the first run. If this is your case, stopped and try again.

2. MockWS and Postgres images are getting from docker hub, and we can run store from IDE. Used to develop time:
```shell
docker-compose -f environment/docker-compose.yml up
```
I added swagger to access: http://localhost:1980.

### To run test

We can run all tests directly from command line. During the process a Postgres and Mock container is started:
```shell
./gradlew test
```

We can run only unit test or only integration tests
```shell
./gradlew unitTests
./gradlew integrationTests
```
I added github actions to run all main commit as CI: https://github.com/iundarigun/my-bank-challenge/actions/workflows/my-bank-challenge-ci.yml

## Technologies
- The main technology are `Java` with `Spring Boot`, with Postgres as relational Database.
  
- I add a flyway to create schema and initial data to play with.

- I add an external communication, mocking an endpoint trying to consult restrictions on transfers between different banks. I use MockWS for this -a project was created for me two years ago-. It allows us to add delays and failing calls. For leading with, I used Resilient4j with CircuitBreaker configuration. I let the window of CircuitBreaker small to see the behavior.

- I used mapStruct to transform object DTO to entity and entity to DTO.

- To Integration tests, I choose to use `RestAssured` with `TestContainers`. The main reason is to have an environment near the real scenario. 

- For Unit tests, I choose `Junit5` with mockito.

- I used a checkstyle configuration to keep the code style uniform.

## Asking questions
In this section, I try to answer some questions

### How would you improve your solution? What would be the next steps?
To improve the solution, I can think some points:
- Add Spring security and roles to allow or denny access to the endpoints.
- Change public id for uuid as unique keys to avoid expose the id
- Add multi-language support to translate exception messages
- Put more logs to make tracing easy.
- Asynchronous processing (in the last question I will explain better)

### After implementation, is there any design decision that you would have done different?
When received the test, I thought the implementation using Hexagonal Architecture. The reason was I am studying this architecture and I saw a good option to practice. I didn't use it for the short time and, mainly, because I found a repository when I was studying that resolves a similar problem and I didn't want copy it. You can see [here](https://github.com/thombergs/buckpal)

I don't know if Hexagonal Architecture is really a good option. Perhaps, for this use case can be better use CQRS. For the test, is an over engineering, but may be a good option for production. I never use it, I only read about but seems good for this scenario.

### How would you adapt your solution if transfers are not instantaneous?
I guess that is the better option. Get request and put in a message broker and process asynchronous. This option allow us to keep entries for failure transfers, retries without leave the client waiting, and so on. 

At this point, we have two options, one using Rabbit and one using Kafka. The decision depends on the case. I would use Kafka if the order is important, and if more microservices are interested in this information. If the order is not important, perhaps Rabbit is a light option with an impressive performance, and by design we have some strategies to lead with retries.  
