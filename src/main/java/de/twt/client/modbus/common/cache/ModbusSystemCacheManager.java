package de.twt.client.modbus.common.cache;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.twt.client.modbus.ontology.ModbusOntology;
import de.twt.client.modbus.ontology.ModbusOntologyModule;

@Service
public class ModbusSystemCacheManager {
	@Value("modbus.ontology.filepath")
	private String filename;
	
	@Value("modbus.ontology.system")
	private String systemName;
	
	private final ModbusOntology ontology = new ModbusOntology();
	
	@PostConstruct
    private void postConstruct() {
		ontology.loadOntology(filename);
	}
	
	private final Logger logger = LogManager.getLogger(ModbusSystemCacheManager.class);
	
	private enum HeadTail {head, tail};
	
	private List<ModbusOntologyModule> getInputOutputModules(HeadTail type) {
		List<ModbusOntologyModule> headTails = new ArrayList<>();
		System.out.println(systemName);
		switch (type) {
		case head: headTails.add(ontology.getInputModuleFromController(systemName)); break;
		case tail: headTails.add(ontology.getOutputModuleFromController(systemName)); break;
		}
		return headTails;
		
	}

	synchronized public String getModbusSystem() {
		return systemName;
	}
	
	synchronized public List<ModbusOntologyModule> getTailModules() {
		return getInputOutputModules(HeadTail.tail);
	}
	
	
	synchronized public List<ModbusOntologyModule> getHeadModules() {
		return getInputOutputModules(HeadTail.head);
	}
}
