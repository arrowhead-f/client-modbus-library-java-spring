package de.twt.client.modbus.provider;

import org.springframework.http.HttpMethod;

public class ProviderConstants {
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "eu.arrowhead.client.provider";
	public static final String ECHO_DEFINITION = "echo";
	public static final String ECHO_URI = "/echo";
	public static final HttpMethod ECHO_HTTP_METHOD = HttpMethod.GET;
	public static final String READ_MODBUS_DATA_SERVICE_DEFINITION = "readModbusData";
	public static final String READ_MODBUS_DATA_URI = "/read";
	public static final HttpMethod READ_MODBUS_DATA_HTTP_METHOD = HttpMethod.POST;
	public static final String WRITE_MODBUS_DATA_SERVICE_DEFINITION = "writeModbusData";
	public static final String WRITE_MODBUS_DATA_URI = "/write";
	public static final HttpMethod WRITE_MODBUS_DATA_HTTP_METHOD = HttpMethod.POST;
	public static final String SET_MODBUS_DATA_CACHE_SERVICE_DEFINITION = "writeModbusDataCache";
	public static final String SET_MODBUS_DATA_CACHE_URI = "/writeToCache";
	public static final HttpMethod SET_MODBUS_DATA_CACHE_HTTP_METHOD = HttpMethod.POST;
	public static final String REQUEST_PARAM_KEY_SLAVEADDRESS = "slaveAddress";
	public static final String $REQUEST_PARAM_SLAVEADDRESS = "${provider." + REQUEST_PARAM_KEY_SLAVEADDRESS + "}";
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	
}
