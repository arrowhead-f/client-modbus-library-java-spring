package de.twt.client.modbus.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import de.twt.client.modbus.common.constants.PackageConstants;
import de.twt.client.modbus.consumer.Consumer;
import de.twt.client.modbus.dataWriter.ModbusDataWriter;
import de.twt.client.modbus.master.MasterTCP;
import de.twt.client.modbus.master.MasterTCPConfig;
import de.twt.client.modbus.publisher.EventModbusData;
import de.twt.client.modbus.publisher.Publisher;
import de.twt.client.modbus.slave.SlaveTCP;
import de.twt.client.modbus.slave.SlaveTCPConfig;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
//		CommonConstants.BASE_PACKAGE, 
		PackageConstants.BASE_PACKAGE_COMMON, 
//		PackageConstants.BASE_PACKAGE_DATAWRITER,
//		PackageConstants.BASE_PACKAGE_PROVIDER,
//		PackageConstants.BASE_PACKAGE_CONSUMER,
//		PackageConstants.BASE_PACKAGE_PUBLISHER,
//		PackageConstants.BASE_PACKAGE_SUBSCRIBER,
//		PackageConstants.BASE_PACKAGE_SLAVE,
//		PackageConstants.BASE_PACKAGE_MASTER
		})
public class App implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
	// publisher and subscriber: enable the corresponding componentScan packages
	
	// data writer
	// @Autowired
	// private ModbusDataWriter modbusDataWriter;
	
	// consumer: to read and write the modbus data from the provider
	// @Autowired
	// private Consumer consumer;
	
	// publisher: ontology and special modbus data
	// @Autowired 
	// private Publisher publisher;
	
	// create the event modbus data
	// @Autowired
	// @Qualifier("configModbusData")
	// private EventModbusData configModbusData;
	
	// create the modbus data event bean to publish
	// @Bean
	// @ConfigurationProperties(prefix="event.modbusdata")
	// public EventModbusData configModbusData() {
	// 	return new EventModbusData();
	// }
	
	// slave config
	// @Autowired
	// @Qualifier("slave")
	// private SlaveTCP slave;
	
	// @Bean
	// public SlaveTCP slave(@Qualifier("slaveTCPConfig") SlaveTCPConfig slaveTCPConfig) {
	// 	return new SlaveTCP(slaveTCPConfig);
	// }
	
	// @Bean
	// @ConfigurationProperties(prefix="slave.app")
	// public SlaveTCPConfig slaveTCPConfig() {
	// 	return new SlaveTCPConfig();
	// }
	
	// master config
	// @Autowired
	// @Qualifier("master")
	// private MasterTCP master;
	
	// @Bean
	// public MasterTCP master(@Qualifier("masterTCPConfig") MasterTCPConfig masterTCPConfig) {
	// 	return new MasterTCP(masterTCPConfig);
	// }
	
	// @Bean
	// @ConfigurationProperties(prefix="master")
	// public MasterTCPConfig masterTCPConfig() {
	// 	return new MasterTCPConfig();
	// }
    
	private final Logger logger = LogManager.getLogger(App.class);
	
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
	
    public static void main( final String[] args ) {
    	SpringApplication.run(App.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
    	logger.info("App started...");
    	// modbusDataWriter.startRecord();
    	// consumer.readDataThread();
    	// consumer.WriteDataThread();
    	// publisher.publishOntology();
    	// publisher.publishModbusDataOnce(configModbusData);
    	// slave.startSlave();
    	// init master to build the connection between master and slave
    	// master.init();
		// master.readDataThreadForEvent();
	}
}
