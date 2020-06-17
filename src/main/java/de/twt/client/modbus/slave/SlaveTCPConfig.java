package de.twt.client.modbus.slave;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;


public class SlaveTCPConfig {
	private RemoteIOData remoteIO;
	private String address;
	private int port;
	private int memoryRange;
	private String readModule;
	private boolean saveInModbusDataCache = false;
	
	public static class RemoteIOData {
		private String address;
		private int port;
		private int offset;
		
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
	}

	public RemoteIOData getRemoteIO() {
		return remoteIO;
	}
	public void setRemoteIO(RemoteIOData remoteIO) {
		this.remoteIO = remoteIO;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMemoryRange() {
		return memoryRange;
	}
	public void setMemoryRange(int memoryRange) {
		this.memoryRange = memoryRange;
	}
	public String getReadModule() {
		return readModule;
	}
	public void setReadModule(String readModule) {
		this.readModule = readModule;
	}
	public boolean getSaveInModbusDataCache() {
		return saveInModbusDataCache;
	}
	public void setSaveInModbusDataCache(boolean saveInModbusDataCache) {
		this.saveInModbusDataCache = saveInModbusDataCache;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
