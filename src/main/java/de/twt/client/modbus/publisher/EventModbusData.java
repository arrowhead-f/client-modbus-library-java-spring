package de.twt.client.modbus.publisher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import de.twt.client.modbus.common.constants.ModbusConstants;

@ConfigurationProperties(prefix="event.modbusdata")
public class EventModbusData {
	
	private String eventType;
	private List<Slave> slaves = new ArrayList<>();
	private int publishingPeriodTime;
	
	public static class Slave {
		private String slaveAddress;
		private List<SlaveData> data = new ArrayList<>();
		
		public static class SlaveData {
			private ModbusConstants.MODBUS_DATA_TYPE type;
			private int startAddress;
			private int length;
			private String module;
			public ModbusConstants.MODBUS_DATA_TYPE getType() {
				return type;
			}
			public void setType(ModbusConstants.MODBUS_DATA_TYPE type) {
				this.type = type;
			}
			public int getStartAddress() {
				return startAddress;
			}
			public void setStartAddress(int startAddress) {
				this.startAddress = startAddress;
			}
			public int getLength() {
				return length;
			}
			public void setLength(int length) {
				this.length = length;
			}
			public String getModule() {
				return module;
			}
			public void setModule(String module) {
				this.module = module;
			}
			
			@Override
			public String toString() {
				return toString("");
			}
			
	        public String toString(String indetation) {
				return indetation + "type: " + type + "\n" +
						indetation + "startAddress: " + startAddress + "\n" +
						indetation + "length: " + length;
			}
		}

		public String getSlaveAddress() {
			return slaveAddress;
		}
		public void setSlaveAddress(String slaveAddress) {
			this.slaveAddress = slaveAddress;
		}
		public List<SlaveData> getData() {
			return data;
		}
		public void setData(List<SlaveData> data) {
			this.data = data;
		}
		
		@Override
        public String toString() {
            return toString("");
        }
		
		public String toString(String indetation) {
            return indetation + "slaveAddress: " + slaveAddress + '\n' 
            		+ dataToString(indetation);
        }
		
		private String dataToString(String indetation) {
			String format = "";
			for (int idx = 0; idx < data.size(); idx++) {
				format += indetation + "data " + idx + ": \n" +
							data.get(idx).toString(indetation + "\t");
			}
			return format;
		}
	}

	public List<Slave> getSlaves() {
		return slaves;
	}
	public void setSlaves(List<Slave> slaves) {
		this.slaves = slaves;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
    public int getPublishingPeriodTime() {
		return publishingPeriodTime;
	}
	public void setPublishingPeriodTime(int publishingPeriodTime) {
		this.publishingPeriodTime = publishingPeriodTime;
	}
	
	public String toString() {
        return "\n" + "eventType: " + eventType + '\n' +
        		"publishingPeriodTime: " + publishingPeriodTime + '\n' + 
        		SlavesToString();
    }
	
	private String SlavesToString(){
		String format = "";
		for (int idx = 0; idx < slaves.size(); idx++) {
			format += "slave " + idx + ": \n" + slaves.get(idx).toString("\t");
		}
		return format;
	}
}
