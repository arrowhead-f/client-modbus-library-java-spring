package de.twt.client.modbus.consumer;

public class ConsumerModbusConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "eu.arrowhead";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String HTTPS_METHOD = "https-method";
	
	public static final String READ_MODBZS_DATA_SERVICE_DEFINITION = "readmodbusdata";
	public static final String WRITE_MODBZS_DATA_SERVICE_DEFINITION = "writeModbusData";
	public static final String REQUEST_PARAM_KEY_SLAVEADDRESS = "slaveAddress";
	
	public static final String THREAD_READ = "thread-read";
	public static final String THREAD_WRITE = "thread-write";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ConsumerModbusConstants() {
		throw new UnsupportedOperationException();
	}
}
