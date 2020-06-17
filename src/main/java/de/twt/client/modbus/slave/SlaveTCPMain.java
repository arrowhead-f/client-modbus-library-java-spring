package de.twt.client.modbus.slave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

// @SpringBootApplication
@EnableConfigurationProperties(SlaveTCPConfig.class)
@ComponentScan(basePackages = {"eu.arrowhead.client.modbus.slave"})
public class SlaveTCPMain implements ApplicationRunner {
	
	@Autowired
    private SlaveTCP slave;
	
	private final Logger logger = LogManager.getLogger(SlaveTCPMain.class);
	
	public static void main( final String[] args ) {
		ApplicationContext app = SpringApplication.run(SlaveTCPMain.class, args);
		/*for(String name: app.getBeanDefinitionNames())
			System.out.println(name + "\n");*/
    }
	
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		logger.debug("start running...");
	}
}
