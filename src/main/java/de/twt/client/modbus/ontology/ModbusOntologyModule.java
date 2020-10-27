package de.twt.client.modbus.ontology;

import de.twt.client.modbus.common.constants.ModbusConstants;

public class ModbusOntologyModule {
	public String name;
	public String ip;
	public ModbusConstants.MODBUS_DATA_TYPE memoryType;
	public int memoryTypeAddress;
	public String defaultValue;
}
