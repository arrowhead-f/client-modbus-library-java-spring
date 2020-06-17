package de.twt.client.modbus.common.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.twt.client.modbus.common.ModbusSystem;

@Service
public class ModbusSystemCacheManager {
	@Autowired
	private ModbusSystem modbusSystem;
	
	private final Logger logger = LogManager.getLogger(ModbusSystemCacheManager.class);
	
	private enum HeadTail {head, tail};

	synchronized public ModbusSystem getModbusSystem() {
		return modbusSystem;
	}
	/*
	synchronized public void setModbusSystem(ModbusSystem modbusSystem) {
		this.modbusSystem = modbusSystem;
	}
	*/
	synchronized public List<ModbusSystem.Module> getTailModules() {
		return getHeadTailModules(HeadTail.tail);
	}
	
	
	synchronized public List<ModbusSystem.Module> getHeadModules() {
		return getHeadTailModules(HeadTail.head);
	}
	
	
	private List<ModbusSystem.Module> getHeadTailModules(HeadTail type) {
		List<ModbusSystem.Module> headTails = new ArrayList<>();
		
		if (!isModbusSystem()) {
			logger.debug("There is no modbus system in this application!");
			return headTails;
		}
		
		List<ModbusSystem.Module> modules = modbusSystem.getModules();
		
		ArrayList<String> headTailsName = new ArrayList<>();
		ArrayList<String> modulesName = new ArrayList<>();
		for (ModbusSystem.Module module : modules) {
			switch (type) {
			case head: headTailsName.add(module.getPreModuleName()); break;
			case tail: headTailsName.add(module.getNextModuleName()); break;
			}
			modulesName.add(module.getName());
		}
		
		for (String moduleName : modulesName) {
			headTailsName.removeIf(name -> (moduleName.equalsIgnoreCase(name)));
		}
		
		for (ModbusSystem.Module module : modules) {
			String name = "";
			switch (type) {
			case head: name = module.getPreModuleName(); break;
			case tail: name = module.getNextModuleName(); break;
			}
			if (headTailsName.contains(name)) {
				headTails.add(module);
			}
		}
		
		return headTails;
	}
	
	private boolean isModbusSystem() {
		if (modbusSystem.getName() == null) {
			return false;
		}
		return true;
	}
}
