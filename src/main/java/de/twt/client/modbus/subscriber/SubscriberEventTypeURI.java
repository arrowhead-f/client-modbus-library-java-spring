package de.twt.client.modbus.subscriber;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "subscriber.event")
public class SubscriberEventTypeURI {
	
	//=================================================================================================
	// members
	
	private Map<String, String> eventTypeURIMap;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	public Map<String, String> getEventTypeURIMap() { return eventTypeURIMap; }
	public void setEventTypeURIMap(final Map<String, String> eventTypeURIMap) { this.eventTypeURIMap = eventTypeURIMap; }
}
