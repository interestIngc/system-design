### Fitness Club app

This web application is a fitness club client management system.  
It consists of three microservices, exposing REST API to:

1. Manage client subscriptions: to create new subscriptions, extend existing subscriptions and retrieve information such as expiry date/time for a particular subscription.
2. Manage turnstile actions: to let clients in and out checking for the validity of their subscriptions.
3. Generate daily reports on clients activity, such as the number of visits and an average duration of a visit.

The application is built on event-sourcing and CQRS design principles, with MongoDB and Spring Boot.
