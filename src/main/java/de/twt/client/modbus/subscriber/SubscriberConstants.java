package de.twt.client.modbus.subscriber;

public class SubscriberConstants {
	//=================================================================================================
	// members
	public static final String MODBUS_DATA_URI = "/" + "modbusData";
	public static final String Module_URI = "/" + "input";
	public static final String DEFAULT_EVENT_NOTIFICATION_BASE_URI = "/notify";
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private SubscriberConstants() {
		throw new UnsupportedOperationException();
	}
}
