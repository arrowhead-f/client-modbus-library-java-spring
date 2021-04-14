package de.twt.client.modbus.publisher;

public class PublisherConstants {
	//=================================================================================================
	// members

	public static final String START_INIT_EVENT_PAYLOAD= "InitStarted";
	public static final String ONTOLOGY_CHANGED = "ontologyChanged";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private PublisherConstants() {
		throw new UnsupportedOperationException();
	}
}
