# Arrowhead Client Modbus Library (Java Spring-Boot)
##### The project provides client modbus library for the Arrowhead Framework 4.1.3

Arrowhead Client Modbus Library contains the dependencies "Arrowhead Client Library" and "jlibmodbus" to provide the data transfer between modbus tcp and http/https. It helps to register the existing modbus slave or master devices to arrowhead core system and enables the http/https communication. 

### Requirements

The project has the following dependencies:
* JRE/JDK 11 [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* Maven 3.5+ [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)
* GitHub Packages [Configuring Maven for GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages)


##### apllication.properties
Location: `src/main/resources`. The defination of the properties will be explained, that can be used for the real application later.
* Custom Parameters: it defines the basic config of the client
    * Change the `client_system_name` property to your system name. *(**Note** that it should be in line with your certificate common name e.g.: when your certificate common name is `my_awesome_client.my_cloud.my_company.arrowhed.eu`, then your system name is  `my_awesome_client`)*
    * Adjust the `server.address` and `server.port` properties based on your system configuration.
    * Adjust the Service Registry Core System location by the `sr_address` and `sr_port` properties.

* Secure mode: it decides if the arrowhead client system runs in secure mode. It depends on Arrowhead Core System. 
    * Decide the required security level and set the `server.ssl.enabled` and `token.security.filter.enabled` properties accordingly.
    * If `server.ssl.enabled` and `token.security.filter.enabled` is set as `true` , [create your own client certificate](https://github.com/arrowhead-f/core-java-spring#certificates) (or for demo purpose use the provided one) and update the further `server.ssl...` properties accordingly. *(**Note** that `server.ssl.key-store-password` and `server.ssl.key-password` must be the same.)*

* Modbus Data Writer: it declares the record content depending on the modbus data.
    * `record_period`: defines the how offen the modbus data will be recorded. default value 200 ms
    * `record.fileName`: the name of the file "*.csv"
    * `record.slaveAddress`: the slave id. The modbus data is from this slave.
    * `record.content[]`: defines the data that needs to be recorded. There are two special data and four kinds of data type (coil, discrete input, holding register and input register). Two special data are *timestemp* which records the time and *slaveAddress* which records the slave id. The number from the data type array means the data address.
    * `record`: it can be changed, in case that more diffenet file can be created at the same time. But the name should be passed to the real application.

* Provider service: it describes the connected modbus slave address which will be registered to arrowhead core system
    * `provider.slaveAddress`: declares the connected modbus slave.

* Modbus System Parameters: it explains the topologie of the production systems
    * `modbus.system.name`: defines the name of the system
    * `modbus.system.modules[]`: defines the modules in the system
    * `modbus.system.modules[0].name`: defines the name of the module
    * `modbus.system.modules[0].preModuleName`: declares the previous module
    * `modbus.system.modules[0].nextModuleName`: declares the next module
    * `modbus.system.modules[0].service[]`: defines services in the module. `modbus.system.modules[0].service[0].name` defines the name of the service. `modbus.system.modules[0].service[0].properties` defines the properties of the service. The Definition of service is not used. It is dessigned for the usage in the future.
    * `modbus.system.modules[0].input`: defines the input of the module from the defined `modbus.system.modules[0].preModuleName`. `modbus.system.modules[0].input.slaveAddress` defines which modbus entity uses the input. `modbus.system.modules[0].input.type` defines in which data type the input should store. `modbus.system.modules[0].address.type` defines in which address from the data type the input should store. `modbus.system.modules[0].input.defaultValue` defines the default if the module doesnot get the information from the previous module.
    * `modbus.system.modules[0].output`: defines the output of the module from the defined `modbus.system.modules[0].nextModuleName`. `modbus.system.modules[0].output.slaveAddress` defines from which slave the data should be used as output. `modbus.system.modules[0].output.type` defines the output data type. `modbus.system.modules[0].output.address` defines the address in data type. `modbus.system.modules[0].output.defaultValue` defines the default value. If there is no data in that data address, the default value will be used.

* Publisher event: it describes the publishing event type, publishing period and publishing data.
    * `event.modbusdata.eventType`: define the event type
    * `event.modbusdata.publishingPeriodTime`: define how often the event will be published.
    * `event.modbusdata.slaves[]`: defines the content of the the event
    * `event.modbusdata.slaves[0].slaveAddress`: defines the slave id where the data comes from.
    * `event.modbusdata.slaves[0].data[]`: defines the exact data information. `event.modbusdata.slaves[0].data[0].type` defines the data type. `event.modbusdata.slaves[0].data[0].startAddress` defines the data starting address. `event.modbusdata.slaves[0].data[0].length` defines the nummer of data. `event.modbusdata.slaves[0].data[0].module` defines the module that the data comes from.
    * `event.modbusdata`: the name can be changed, but it needs to be passed to the real application.

* Subscriber event handling:
    * `event.eventTypeURIMap.{YOUR_EVENT_TYPE}={notificationuri for YOUR_EVENT_TYPE}`: defines data type and uri

* modbus slave parameters:
    * `slave.app.remoteIO`: defines the basic config of the remote IO that the connected master wants to read or write. `slave.app.remoteIO.address` and `slave.app.remoteIO.port` defines the remote IO IP address and port. `slave.app.remoteIO.offset` defines the offset between the data address from connected master and stored in remote IO.
    * `slave.app.port`: defines the port of the slave.
    * `slave.app.memoryRange`: defines the max memory of each data type
    * `slave.app.readModule`: defines how to get the data from other arrowhead modbus client. There are two types here, namely *evnet* and *service*, that defines uses consumer service oder subscriber event to get the data. Notes: If the network speed is not fast enough, *service* can not be used. 
    * `slave.app`: the name can be changed, but it needs to be passed to the real application.

* modbus master parameters:
    * `master.data.{DATA_ACCESS_OPTION}.{DATA_TYPE}.{DATA_RANGE_DEFINITION}={DATA_ADDRESS}`: defines the data access option information. *DATA_ACCESS_OPTION* defines to *read* or *write* the data. *DATA_TYPE* defines the data type. *DATA_RANGE_DEFINITION* defines the *start* and *end* address of the data. *DATA_ADDRESS* defines the data address.
    * `master.slave`: declares the connected modbus slave information. `master.slave.address` and `master.slave.port` declares the slave's IP address and port.
    * `master.periodTime`: defines how often the modbus master operates the data access options.

##### Usage by Application
This Arrowhead Modbus Client Libraray can be seperated into seven parts, namely sevice provider/consumer, event publisher/subscriber, modbus slave/master, data recorder. They can be combined randomly according to different commands to meet tasks in various situations. Depending on the Combination, the application.properties file needs to be redefined with corresponding parts as well. However, there are still some restrictions like following:

* *Custom Parameters* and *Secure mode* must be defines by every application
* If the service provider is used, the modbus slave should be also applied by the same client.
* If the modbus slave is used, the service consumer should be alse applied by the same client.

