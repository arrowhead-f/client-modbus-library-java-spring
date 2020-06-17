package de.twt.client.modbus.publisher;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.twt.client.modbus.common.ModbusData;
import de.twt.client.modbus.common.ModbusSystem;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.cache.ModbusSystemCacheManager;
import de.twt.client.modbus.common.constants.EventConstants;
import de.twt.client.modbus.common.constants.ModbusConstants;
import de.twt.client.modbus.publisher.EventModbusData.Slave;
import de.twt.client.modbus.publisher.EventModbusData.Slave.SlaveData;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventPublishRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;

@Component
public class Publisher {
	
	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String clientSystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String clientSystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int clientSystemPort;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ModbusSystemCacheManager modbusSystemCacheManager;
	
	private final Logger logger = LogManager.getLogger(Publisher.class);
	private boolean stopPublishing;
	private EventModbusData configModbusData;
	private SystemRequestDTO source;
	
	//@PostConstruct
	private void postConstruct() {
		createSystemRequestDTO();
	}
	
	//-------------------------------------------------------------------------------------------------
	// Equipment Ontology event
	
	public void publishOntology() {
		logger.debug("start publishing module event regularly...");
		createSystemRequestDTO();
		List<ModbusSystem.Module> tails = modbusSystemCacheManager.getTailModules();
		if (tails.size() == 0) {
			logger.info("this is already the end of production.");
		}
		
		new Thread(){
			public void run(){
				while(!stopPublishing){
					for (ModbusSystem.Module tail : tails) {
						publishOntologyOutput(tail);
					}

					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	
	private void publishOntologyOutput(ModbusSystem.Module module) {
		if (module.getNextModuleName() == null || module.getNextModuleName() == "") {
			return;
		}
		
		ModbusSystem.Module.DataInterface output = module.getOutput();
		
		if (output == null) {
			logger.warn("there is no output data at the last module {}.", module.getName());
			return;
		}
		
		String slaveAddress = output.getSlaveAddress();
		if (!ModbusDataCacheManager.containsSlave(slaveAddress)) {
			logger.warn("The slave ({}) does not exist in the modbus data cache.", slaveAddress);
			return;
		}
		
		String payload = "";
		final int address = output.getAddress();
		switch (output.getType()) {
		case coil: 
			payload = ModbusDataCacheManager.getCoils(slaveAddress).get(address).toString(); break;
		case discreteInput: 
			payload = ModbusDataCacheManager.getDiscreteInputs(slaveAddress).get(address).toString(); break;
		case holdingRegister: 
			payload = ModbusDataCacheManager.getHoldingRegisters(slaveAddress).get(address).toString(); break;
		case inputRegister: 
			payload = ModbusDataCacheManager.getInputRegisters(slaveAddress).get(address).toString(); break;
		}
		final String eventType = module.getName();
		final Map<String,String> metadata = new HashMap<String,String>();
		final String timeStamp = Utilities.convertZonedDateTimeToUTCString(ZonedDateTime.now());
		final EventPublishRequestDTO publishRequestDTO = new EventPublishRequestDTO(eventType, source, metadata, payload, timeStamp);
		arrowheadService.publishToEventHandler(publishRequestDTO);
		logger.info("publish event {} successfully!", eventType);
	}
	
	//-------------------------------------------------------------------------------------------------
	// Modbus Data event
	
	// publish all events (modbus data) regularly which are described in PublisherConfig
	public void publishModbusData(EventModbusData configModbusData){
		logger.debug("start publishing modbus data event regularly...");
		this.configModbusData = configModbusData;
		stopPublishing = false;
		new Thread(){
			public void run(){
				while(!stopPublishing){
					publishModbusDataOnce();
				}
			}
		}.start();
	}
	
	// publish all events (modbus data) only once which are described in PublisherConfig (different slaves)
	private void publishModbusDataOnce() { 
		logger.debug("publish modbus data event once...");
		List<Slave> slaves = configModbusData.getSlaves();
		for (int idx = 0; idx < slaves.size(); idx++) {
			publishModbusDataOnceWithSlaveAddress(slaves.get(idx));
		}
	}
	
	public void publishModbusDataOnce(EventModbusData configModbusData) { 
		logger.info("publish modbus data event once...");
		this.configModbusData = configModbusData;
		List<Slave> slaves = configModbusData.getSlaves();
		for (int idx = 0; idx < slaves.size(); idx++) {
			publishModbusDataOnceWithSlaveAddress(slaves.get(idx));
		}
	}
	
	// get related data (certain slave) from modbus data cache 
	private void publishModbusDataOnceWithSlaveAddress(Slave slaveConfig){
		final String slaveAddress = slaveConfig.getSlaveAddress();
		if (!ModbusDataCacheManager.containsSlave(slaveAddress)) {
			logger.warn("The slave ({}) does not exist in the modbus data cache.", slaveAddress);
			return;
		}
		final String eventType = configModbusData.getEventType();
		ModbusData modbusData = new ModbusData();
		List<SlaveData> slaveData = slaveConfig.getData();
		for (int idx = 0; idx < slaveData.size(); idx++) {
			setModbusData(modbusData, slaveAddress, slaveData.get(idx));
		}
		final String payload = Utilities.toJson(modbusData);
		final Map<String,String> metadata = new HashMap<String,String>();
		metadata.put(EventConstants.MODBUS_DATA_METADATA_SLAVEADDRESS, slaveAddress);
		final String timeStamp = Utilities.convertZonedDateTimeToUTCString(ZonedDateTime.now());
		final EventPublishRequestDTO publishRequestDTO = new EventPublishRequestDTO(eventType, source, metadata, payload, timeStamp);
		arrowheadService.publishToEventHandler(publishRequestDTO);
	}
	
	// create the system request dto
	private void createSystemRequestDTO(){
		logger.debug("create system request dto...");
		if (source != null) {
			return;
		}
		source = new SystemRequestDTO();
		source.setSystemName(clientSystemName);
		source.setAddress(clientSystemAddress);
		source.setPort(clientSystemPort);
		if (sslEnabled) {
			source.setAuthenticationInfo(Base64.getEncoder().encodeToString( arrowheadService.getMyPublicKey().getEncoded()));
		}
	}
	
	private void setModbusData(ModbusData modbusData, String slaveAddress, SlaveData slaveDataConfig) {
		final ModbusConstants.MODBUS_DATA_TYPE type = slaveDataConfig.getType();
		final int startAddress = slaveDataConfig.getStartAddress();
		final int length = slaveDataConfig.getLength();
		
		switch(type){
		case coil: 
			modbusData.setCoils(getFilteredData(ModbusDataCacheManager.getCoils(slaveAddress), startAddress, length)); break;
		case discreteInput: 
			modbusData.setDiscreteInputs(getFilteredData(ModbusDataCacheManager.getDiscreteInputs(slaveAddress), startAddress, length)); break;
		case holdingRegister: 
			modbusData.setHoldingRegisters(getFilteredData(ModbusDataCacheManager.getHoldingRegisters(slaveAddress), startAddress, length)); break;
		case inputRegister: 
			modbusData.setInputRegisters(getFilteredData(ModbusDataCacheManager.getInputRegisters(slaveAddress), startAddress, length)); break;
		}
	}
	
	private <T> HashMap<Integer, T> getFilteredData(HashMap<Integer, T> data, int startAddress, int length){
		HashMap<Integer, T> filteredData = new HashMap<Integer, T>();
		for(int idx = 0; idx < length; idx++) {
			int id = idx + startAddress;
			filteredData.put(id, data.get(id));
		}
		return filteredData;
	}
	/*
	private String getEventPayload(String slaveAddress, String type, int startAddress, int length) {
		logger.debug("generate the event playload...");
		String payload = "";
		switch(type){
		case EventConstants.MODBUS_DATA_METADATA_TYPE_COIL: 
			payload = getEventPayload(ModbusDataCacheManager.getCoils(slaveAddress), startAddress, length); break;
		case EventConstants.MODBUS_DATA_METADATA_TYPE_DISCRETE_INPUT: 
			payload = getEventPayload(ModbusDataCacheManager.getDiscreteInputs(slaveAddress), startAddress, length); break;
		case EventConstants.MODBUS_DATA_METADATA_TYPE_HOLDING_REGISTER: 
			payload = getEventPayload(ModbusDataCacheManager.getHoldingRegisters(slaveAddress), startAddress, length); break;
		case EventConstants.MODBUS_DATA_METADATA_TYPE_INPUT_REGISTER: 
			payload = getEventPayload(ModbusDataCacheManager.getInputRegisters(slaveAddress), startAddress, length); break;
		}
		return payload;
	}
	
	private <T> String getEventPayload(HashMap<Integer, T> data, int startAddress, int length){
		String payload = "";
		for(int idx = startAddress; idx < length; idx++) {
			payload += data.get(idx).toString() + ',';
		}
		return payload.substring(0, payload.length()-1);
	}
	*/
}
