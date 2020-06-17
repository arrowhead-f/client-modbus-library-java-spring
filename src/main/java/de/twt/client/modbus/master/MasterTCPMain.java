package de.twt.client.modbus.master;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.twt.client.modbus.master.MasterTCPConfig.Data;

//@Configuration
@ComponentScan
@EnableConfigurationProperties(MasterTCPConfig.class)
public class MasterTCPMain {
	public static void main( final String[] args ) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MasterTCPMain.class);
		MasterTCPConfig app = ctx.getBean(MasterTCPConfig.class);
		MasterTCP master = ctx.getBean(MasterTCP.class);
		//master.setModbusMaster();
		//master.readDataThread();
		System.out.println(app.getData().getRead().getCoils());
		ctx.close();
	}
}
