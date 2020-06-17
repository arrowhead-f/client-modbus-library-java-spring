package de.twt.client.modbus.consumer;

public class ConsumerModbusConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "eu.arrowhead";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String HTTPS_METHOD = "https-method";
	
	public static final String READ_MODBUS_DATA_SERVICE_DEFINITION = "readmodbusdata";
	public static final String WRITE_MODBUS_DATA_SERVICE_DEFINITION = "writeModbusData";
	public static final String REQUEST_PARAM_KEY_SLAVEADDRESS = "slaveAddress";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ConsumerModbusConstants() {
		throw new UnsupportedOperationException();
	}
}
