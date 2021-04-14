package de.twt.client.modbus.subscriber;


import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.twt.client.modbus.common.ModbusData;
import de.twt.client.modbus.common.ModbusSystem;
import de.twt.client.modbus.common.OntologyChangedEvent;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.cache.ModbusSystemCacheManager;
import de.twt.client.modbus.common.constants.EventConstants;
import de.twt.client.modbus.ontology.ModbusOntologyModule;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventDTO;

@RestController
@RequestMapping(SubscriberConstants.DEFAULT_EVENT_NOTIFICATION_BASE_URI)
public class SubscriberController {
	//=================================================================================================
	// members
	
	@Autowired
	private ModbusSystemCacheManager modbusSystemCacheManager;

	private final Logger logger = LogManager.getLogger(SubscriberController.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here additional subscriber related REST end points
	@PostMapping(path = SubscriberConstants.MODBUS_DATA_URI) 
	public void receivePublsisherEventModbusData(@RequestBody final EventDTO event) {
		logger.debug("receivePublsisherEventModbusData started... ");
		if (event.getEventType() == null) {			
			logger.warn("EventType is null.");
			return;
		}
		
		Map<String, String> metadata = event.getMetaData();
		ModbusData modbusDataEvent = Utilities.fromJson(event.getPayload(), ModbusData.class); 
		final String slaveAddress = metadata.get(EventConstants.MODBUS_DATA_METADATA_SLAVEADDRESS);
		ModbusDataCacheManager.setModbusData(slaveAddress, modbusDataEvent);
	}
	
	
	@PostMapping(path = SubscriberConstants.Module_URI) 
	public void receivePublsisherEventModule(@RequestBody final EventDTO event) {
		logger.debug("receivePublsisherEventModule started... ");
		if (event.getEventType() == null) {			
			logger.warn("EventType is null.");
			return;
		}
		if (modbusSystemCacheManager.getModbusSystem() == null) {
			logger.warn("There is no data in modbus system!");
			return;
		}
		List<ModbusOntologyModule> modules = modbusSystemCacheManager.getHeadModules();
		ModbusOntologyModule module = null;
		for (int id = 0; id < modules.size() ; id++ ) {
			if (modules.get(id) == null || modules.get(id).name == null) {
				continue;
			}
			if (modules.get(id).name.equalsIgnoreCase(event.getEventType())) {
				module = modules.get(id);
				break;
			}
		}
		if (module == null) {
			logger.warn("There is no component that matches this event {}!", event.getEventType());
			return;
		}

		ModbusDataCacheManager.setModbusData(module.ip, module.memoryType, 
				module.memoryTypeAddress, event.getPayload());
	}
	
	@PostMapping(path = SubscriberConstants.ONTOLOGY_CHANGED_URI) 
	public void receivePublsisherOntologyChanged(@RequestBody final EventDTO event) {
		logger.debug("receivePublsisherOntologyChanged started... ");
		System.out.println("receivePublsisherOntologyChanged");
		if (event.getEventType() == null) {			
			logger.warn("EventType is null.");
			return;
		}
		String systemName = modbusSystemCacheManager.getModbusSystem();
		if (systemName == null) {
			logger.warn("There is no data in modbus system!");
			return;
		}
		
		OntologyChangedEvent ontologyChangedEvent = Utilities.fromJson(event.getPayload(), OntologyChangedEvent.class);
		if (!ontologyChangedEvent.getSystemNames().contains(systemName)) {
			logger.info("ontology system {} is ignored bei ontology change event {}.", systemName, ontologyChangedEvent.getSystemNames().toString());
			return;
		}

		modbusSystemCacheManager.setNewFilename(ontologyChangedEvent.getFilename());
	}
}
