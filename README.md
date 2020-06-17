# Arrowhead Client Skeletons (Java Spring-Boot)
##### The project provides client skeletons for the Arrowhead Framework 4.1.3

### How to use client skeletons?

Fork this repo and extend the skeletons with your own application code. ([check the best practice recommendations](https://github.com/arrowhead-f/client-skeleton-java-spring/blob/master/README.md#best-practices-to-start-with-the-skeletons))

### Requirements

The project has the following dependencies:
* JRE/JDK 11 [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* Maven 3.5+ [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)

### Project structure

This is a multi-module maven project relying on the [parent `pom.xml`](https://github.com/arrowhead-f/client-skeleton-java-spring/blob/master/pom.xml) which lists all the modules and common dependencies.

##### Modules:

* **client-skeleton-consumer**: client skeleton module with the purpose of initiating an orchestration request and consume the service from the chosen provider. This consumer project also contains a simple example of how to orchestrate and consume the service afterward.

* **client-skeleton-provider**: client skeleton module with the purpose of registering a specific service into the Service Registry and running a web server where the service is available.

* **client-skeleton-subscriber**: client skeleton module with the purpose of registering subscriptions into the Event Handler and running a web server where it waits for notifications.

* **client-skeleton-publisher**: client publisher module with the purpose of publishing events into the Event Handler.

Skeletons are built on the [`Arrowhead Client Library`](https://github.com/arrowhead-f/client-library-java-spring) which is also imported to this project as a Maven dependency. The client library provides the `ArrowheadService.class` which is a singleton spring managed bean and designed with the purpose of interacting with Arrowhead Framework. Use its methods by [autowiring](https://www.baeldung.com/spring-autowire) into your spring managed custom classes or use `ArrowheadBeans.getArrowheadService()` if your custom class is not spring managed. *(**Look for the java docs** attached for each method within this class.)*

Each client skeleton has a default 'ApplicationInitListener' and a default 'SecurityConfig' what you can change or extend. The essential configuration has to be managed by customizing the `application.properties` file, located in `src/main/resources` folder.

### Best practices to start with the skeletons

##### (1st) apllication.properties
Location: `src/main/resources`
* Decide the required security level and set the `server.ssl.enabled` and `token.security.filter.enabled` properties accordingly.
* [Create](https://github.com/arrowhead-f/core-java-spring#certificates) your own client certificate (or for demo purpose use the provided one) and update the further `server.ssl...` properties accordingly. *(**Note** that `server.ssl.key-store-password` and `server.ssl.key-password` must be the same.)*
* Change the `client_system_name` property to your system name. *(**Note** that it should be in line with your certificate common name e.g.: when your certificate common name is `my_awesome_client.my_cloud.my_company.arrowhed.eu`, then your system name is  `my_awesome_client`)*
* Adjust the Service Registry Core System location by the `sr_address` and `sr_port` properties.
* In case of a provider you have to set its web-server parameters by the `server.address` and `server.port` properties.
* In case of a consumer decide whether it should act as a web-server or not. If yes, then set the `spring.main.web-application-type` to 'servlet' and set further server parameters like in the provider case. If not, just leave these properties unchanged.
* In case of a subscriber you have to set its web-server parameters by the `server.address` and `server.port` properties.
* In case of a subscriber you should set event type - notification URI pair properties as `event.eventTypeURIMap.{YOUR_EVENT_TYPE}={notificationuri for YOUR_EVENT_TYPE}`.
* In case of a publisher you have to set its web-server parameters by the `server.address` and `server.port` properties.
* In case of a publisher decide whether it should act as a web-server or not. If not, then set the `spring.main.web-application-type` to 'none'. 

##### (2nd) package structure
All the provided skeleton classes are located in the child packages of the `eu.arrowhead` base package.
* You can create your own classes  under this base package or
* You can create your own packages like `com.my_company.my_awesome_project` to organize the skeleton and the application code separated. In the latter case if you wish to use Spring Beans at your custom packages, then you have to let the Spring Framework to known about your base package(s). This can be managed by adding the base package name(s) as a string value(s) in the `@ComponentScan` annotation of the application's `Main.class` *(**Look for the 'TODO' mark** within the main class)*.

##### (3rd) security configuration
The skeletons provide a built-in arrowhed framework compatible security configuration located in `eu.arrowhead.client.skeleton.consumer|provider.security` package.
* The `ConsumerSecurityConfig.class`, the `ProviderSecurityConfig.class`, the `SubscriberSecurityConfig.class` and the `PublisherSecurityConfig.class` extends the `DefaultSecurityConfig.class` which is imported by the client-library dependency and responsible for setting the `server.ssl.enabled` property declared in the `application.properties`. *(**Note:** The `ConsumerSecurityConfig.class` is became effective only when your consumer is a web-server.)*
* The `ConsumerAccessControlFilter.class` the `ProviderSecurityConfig.class`, the `SubscriberSecurityConfig.class` and the `PublisherSecurityConfig.class` extends the `AccessControlFilter.class` which is imported by the client-library dependency and responsible for setting the security level based on the `application.properties`.
  -  `ConsumerAccessControlFilter.class` is effective only when your consumer is a web-server and `server.ssl.enabled` property is set to true. This filter is responsible for validating whether the received HTTPS request is coming from one of the local cloud's clients based on its certificate. *(**Look for the 'TODO' mark** within this class if you want to implement additional access rules.)*
  -  `ProviderAccessControlFilter.class` is doing the same as described in the consumer case, but is effective only when `server.ssl.enabled` property is set to true and `token.security.filter.enabled` property is set to false. When `token.security.filter.enabled` property is set to true, then `ProviderTokenSecurityFilter.class` is effective which is validating whether a token is received within the HTTPS request and whether it is a valid one ore not. *(**Note** that the token is created by the Authorization Core System during the orchestration process and the consumer have to put it into its HTTPS request as a query parameter.)*
  -  `PublisherAccessControlFilter.class` is doing the same as described in the provider case, but when `token.security.filter.enabled` property is set to true, then `PublisherTokenSecurityFilter.class` is effective and is doing the same as described in the provider case.
  -  `SubscriberAccessControlFilter.class` is doing the same as described in the provider case, but when `token.security.filter.enabled` property is set to true, then `SubscriberTokenSecurityFilter.class` is effective.
* The `SubscriberTokenSecurityFilter.class` is only checking the token if the requested target URI is NOT a notification URI. Notfication URIs are specified in the application.properties file.
* The `SubscriberNotificationAccessControlFilter.class` is checking the requested target URI and if it is registered in the application.properties as notification URI then validating whether the requester is an allowed Core System. If the requester is not allowed, it throws an AuthExeption with `" is unauthorized to access "` text in the message body. *(**Note:** By default only the `EVENT HANDLER CORE SYSTEM` is allowed.)* 

##### (4th) start-up & shutdown configuration
The skeletons provide a built-in application start-up and shutdown configuration located in `eu.arrowhead.client.skeleton.consumer|provider` package.
The `ConsumerApplicationInitListener.class`, the `ProviderApplicationInitListener.class`, `PublisherApplicationInitListener.class` and the `SubscriberApplicationInitListener.class` contains the `customInit()` method which is executed automatically right after the application start-up and also the `customDestroy()` method which is executed automatically right after triggering the application shutdown, but still before the final stop. *(**Look for the 'TODO' marks** within these classes if you want to implement additional logic.)*

###### Already implemented Consumer start-up logic:
* Checking the Service Registry Core System reachability. *(Sends an 'echo' request to the server.)*
* Checking the Orchestrator Core System reachability. *(Sends an 'echo' request to the server.)*
* Querying and storing the public service URIs of Orchestrator Core System. *(Sends 'query' requests to the Service Registry.)*

###### Already implemented Provider start-up logic:
* Checking the Service Registry Core System reachability. *(Sends an 'echo' request to the server.)*
* Checking the Authorization Core System reachability if 'TokenSecurityFilter' enabled. *(Sends an 'echo' request to the server.)*
* Querying and storing the 'public-key' service URI of Authorization Core System if token security filter is enabled. *(Sends a 'query' request to the Service Registry.)*
* Turning on the token security filter if it is enabled.

###### Recommended Provider start-up logics:
* Registering the provided service into the Service Registry Core System. *(**Hint:** Use the `forceRegisterServiceToServiceRegistry()` method from `ArrowheadService.class`. It removes your current service registry entry from the database and registers again, so it ensures that if your service interfaces or the metadata have been changed, then the freshest condition will be published.)*

###### Recommended Provider shutdown logic:
* Unregistering the service from Service Registry Core System. *(**Hint:** Use the `unregisterServiceFromServiceRegistry()` method from `ArrowheadService.class`.)*

###### Already implemented Publisher start-up logics:
* Checking the Service Registry Core System reachability. *(Sends an 'echo' request to the server.)*
* Checking the Authorization Core System reachability if 'TokenSecurityFilter' enabled. *(Sends an 'echo' request to the server.)*
* Checking the Event Handler Core System reachability. *(Sends an 'echo' request to the server.)*
* Querying and storing the 'public-key' service URI of Authorization Core System if token security filter is enabled. *(Sends a 'query' request to the Service Registry.)*
* Turning on the token security filter if it is enabled.
* Publishing an event with `START_INIT` event type and `"InitStarted"` payload when the start-up is successful.

###### Already implemented Subscriber start-up logics:
* Checking the Service Registry Core System reachability. *(Sends an 'echo' request to the server.)*
* Checking the Authorization Core System reachability if 'TokenSecurityFilter' enabled. *(Sends an 'echo' request to the server.)*
* Checking the Event Handler Core System reachability. *(Sends an 'echo' request to the server.)*
* Querying and storing the 'public-key' service URI of Authorization Core System if token security filter is enabled. *(Sends a 'query' request to the Service Registry.)*
* Setting eventTypeMap field of token security filter.
* Turning on the token security filter if it is enabled.
* Turning on the notification filter.
* Subscribing to the event types defined in apllication.properties.

###### Recommended Subscriber start-up order
* Register Subscriber system in Service Registry (through swagger)
* Authorize Subscriber to Publisher in Authorization (through swagger)
* Start Subscriber (subscribe to events)

###### Already implemented Subscriber shutdown logics:
* Unsubscribing from the event types defined in apllication.properties.

##### Check [`sos-examples-spring`](https://github.com/arrowhead-f/sos-examples-spring) repository for full demo client implementations.
