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
	
	
	@Value("${modbus.ontology.system}")
	private String systemName;
	@Value("${modbus.ontology.filename}")
	private String filename;
	
	private ModbusOntology ontology = new ModbusOntology();
	
	@PostConstruct
    private void postConstruct() {
		ontology.loadOntology(filename);
	}
	
	synchronized public void setNewFilename(String filename) {
		this.filename = filename;
		ontology = new ModbusOntology();
		ontology.loadOntology(filename);
	}
	
	synchronized public void setNewsystemName(String systemName) {
		this.systemName = systemName;
	}
	
	private final Logger logger = LogManager.getLogger(ModbusSystemCacheManager.class);
	
	private enum HeadTail {head, tail};
	
	private List<ModbusOntologyModule> getInputOutputModules(HeadTail type) {
		List<ModbusOntologyModule> headTails = new ArrayList<>();
		switch (type) {
		case head: 
			ModbusOntologyModule inputModule = ontology.getInputModuleFromController(systemName); 
			if (inputModule != null) headTails.add(inputModule); 
			break;
		case tail: 
			ModbusOntologyModule outputModule = ontology.getOutputModuleFromController(systemName); 
			if (outputModule != null) headTails.add(outputModule); 
			break;
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
