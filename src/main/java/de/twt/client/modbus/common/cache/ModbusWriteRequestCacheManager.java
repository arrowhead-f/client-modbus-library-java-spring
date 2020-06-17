package de.twt.client.modbus.common.cache;

import java.util.HashMap;

import de.twt.client.modbus.common.ModbusWriteRequestDTO;

public class ModbusWriteRequestCacheManager {
	private final static HashMap<String, ModbusWriteRequestDTO> modbusWriteRequestCaches = new HashMap<String, ModbusWriteRequestDTO>();
	private final static HashMap<String, Boolean> modbusWriteRequestImplementedCaches = new HashMap<String, Boolean>();
	
	synchronized static public void putWriteRequest(String slaveAddress, ModbusWriteRequestDTO request){
		modbusWriteRequestCaches.put(slaveAddress, request);
		modbusWriteRequestImplementedCaches.put(slaveAddress, Boolean.FALSE);
	}
	
	synchronized static public ModbusWriteRequestDTO getWriteRequest(String slaveAddress){
		return modbusWriteRequestCaches.get(slaveAddress);
	}
	
	synchronized static public ModbusWriteRequestDTO getWriteRequestToImplement(String slaveAddress){
		modbusWriteRequestImplementedCaches.put(slaveAddress, Boolean.TRUE);
		return getWriteRequest(slaveAddress);
	}
	
	synchronized static public boolean isImplemented(String slaveAddress){
		if (!modbusWriteRequestImplementedCaches.containsKey(slaveAddress)){
			return true;
		}
		return modbusWriteRequestImplementedCaches.get(slaveAddress).booleanValue();
	}
}
