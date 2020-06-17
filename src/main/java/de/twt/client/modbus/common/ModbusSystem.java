package de.twt.client.modbus.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import de.twt.client.modbus.common.constants.ModbusConstants;

@Repository
@Configuration
@ConfigurationProperties(prefix = "modbus.system")
public class ModbusSystem {
	private String name;
	private List<Module> modules = new ArrayList<>();
	
	public static class Module {
		private String name;
		private String preModuleName;
		private String nextModuleName;
		private DataInterface input;
		private DataInterface output;
		private List<Service> service = new ArrayList<>();
		
		public static class Service {
			private String name;
			private Map<String, String> properties;
			
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public Map<String, String> getProperties() {
				return properties;
			}
			public void setProperties(Map<String, String> properties) {
				this.properties = properties;
			}
		}
		
		public static class DataInterface {
			private String slaveAddress;
			private ModbusConstants.MODBUS_DATA_TYPE type;
			private int address;
			private String defaultValue;
			
			public String getSlaveAddress() {
				return slaveAddress;
			}
			public void setSlaveAddress(String slaveAddress) {
				this.slaveAddress = slaveAddress;
			}
			public ModbusConstants.MODBUS_DATA_TYPE getType() {
				return type;
			}
			public void setType(ModbusConstants.MODBUS_DATA_TYPE type) {
				this.type = type;
			}
			public int getAddress() {
				return address;
			}
			public void setAddress(int address) {
				this.address = address;
			}
			public String getDefaultValue() {
				return defaultValue;
			}
			public void setDefaultValue(String defaultValue) {
				this.defaultValue = defaultValue;
			}
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPreModuleName() {
			return preModuleName;
		}
		public void setPreModuleName(String preModuleName) {
			this.preModuleName = preModuleName;
		}
		public String getNextModuleName() {
			return nextModuleName;
		}
		public void setNextModuleName(String nextModuleName) {
			this.nextModuleName = nextModuleName;
		}
		public DataInterface getInput() {
			return input;
		}
		public void setInput(DataInterface input) {
			this.input = input;
		}
		public DataInterface getOutput() {
			return output;
		}
		public void setOutput(DataInterface output) {
			this.output = output;
		}
		public List<Service> getService() {
			return service;
		}
		public void setService(List<Service> service) {
			this.service = service;
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Module> getModules() {
		return modules;
	}
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
}
