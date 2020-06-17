package de.twt.client.modbus.subscriber;

import eu.arrowhead.common.dto.shared.SubscriptionRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;

public class SubscriberUtilities {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static SubscriptionRequestDTO createSubscriptionRequestDTO(final String eventType, final SystemRequestDTO subscriber, final String notificationUri) {
		
		final SubscriptionRequestDTO subscription = new SubscriptionRequestDTO(eventType.toUpperCase(), 
																			   subscriber, 
																			   null, 
																			   SubscriberConstants.DEFAULT_EVENT_NOTIFICATION_BASE_URI + "/" + notificationUri, 
																			   false, 
																			   null, 
																			   null, 
																			   null);		
		return subscription;
	}
}
