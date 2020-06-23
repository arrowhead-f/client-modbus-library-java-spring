# Arrowhead Client Modbus Library (Java Spring-Boot)
##### The project provides client modbus library for the Arrowhead Framework 4.1.3

Arrowhead Client Modbus Library contains the dependencies "Arrowhead Client Library" and "jlibmodbus" to provide the data transfer between modbus tcp and http/https.

### Requirements

The project has the following dependencies:
* JRE/JDK 11 [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* Maven 3.5+ [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)
* GitHub Packages [Configuring Maven for GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages)


##### (1st) apllication.properties
Location: `src/main/resources`
* Decide the required security level and set the `server.ssl.enabled` and `token.security.filter.enabled` properties accordingly.
* [Create](https://github.com/arrowhead-f/core-java-spring#certificates) your own client certificate (or for demo purpose use the provided one) and update the further `server.ssl...` properties accordingly. *(**Note** that `server.ssl.key-store-password` and `server.ssl.key-password` must be the same.)*
* Change the `client_system_name` property to your system name. *(**Note** that it should be in line with your certificate common name e.g.: when your certificate common name is `my_awesome_client.my_cloud.my_company.arrowhed.eu`, then your system name is  `my_awesome_client`)*
* Adjust the Service Registry Core System location by the `sr_address` and `sr_port` properties.
...
