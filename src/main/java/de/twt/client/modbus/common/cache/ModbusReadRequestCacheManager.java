package de.twt.client.modbus.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.twt.client.modbus.common.ModbusReadRequestDTO;

public class ModbusReadRequestCacheManager {
	private final static HashMap<String, List<ModbusReadRequestDTO>> modbusReadRequestCaches = new HashMap<String, List<ModbusReadRequestDTO>>();
	
	synchronized static public void putReadRequest(String slaveAddress, ModbusReadRequestDTO request){
		if (!modbusReadRequestCaches.containsKey(slaveAddress)){
			modbusReadRequestCaches.put(slaveAddress, new ArrayList<ModbusReadRequestDTO>());
		}
		modbusReadRequestCaches.get(slaveAddress).add(request);
	}
	
	synchronized static public ModbusReadRequestDTO getFirstReadRequest(String slaveAddress){
		if (!checkFirstReadRequest(slaveAddress)){
			return null;
		}
		return modbusReadRequestCaches.get(slaveAddress).get(0);
	}
	
	synchronized static public void deleteFirstReadRequest(String slaveAddress){
		if (!checkFirstReadRequest(slaveAddress)){
			return;
		}
		modbusReadRequestCaches.get(slaveAddress).remove(0);
	}
	
	synchronized static public void deleteReadRequest(String slaveAddress, String id) {
		if (!modbusReadRequestCaches.containsKey(slaveAddress)){
			return;
		}
		
		List<ModbusReadRequestDTO> requests =  modbusReadRequestCaches.get(slaveAddress);
		for (int index = 0; index < requests.size(); index++) {
			if (id == requests.get(index).getID()) {
				requests.remove(index);
				break;
			}
		}
	}
	
	synchronized static public boolean isEmpty(String slaveAddress){
		return !checkFirstReadRequest(slaveAddress);
	}
	
	synchronized static public boolean isIDExist(String slaveAddress, String id){
		if (!modbusReadRequestCaches.containsKey(slaveAddress)){
			return false;
		}
		List<ModbusReadRequestDTO> requests =  modbusReadRequestCaches.get(slaveAddress);
		for (ModbusReadRequestDTO request : requests) {
			if (id == request.getID()) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkFirstReadRequest(String slaveAddress){
		if (!modbusReadRequestCaches.containsKey(slaveAddress)){
			return false;
		}
		if (modbusReadRequestCaches.get(slaveAddress).isEmpty()){
			return false;
		}
		return true;
	}
}
