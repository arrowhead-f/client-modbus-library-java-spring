package de.twt.client.modbus.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.common.core.CoreSystem;

@Component
public class ConsumerApplicationInitListener extends ApplicationInitListener {

	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		
		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		checkCoreSystemReachability(CoreSystem.ORCHESTRATOR);
		checkCoreSystemReachability(CoreSystem.AUTHORIZATION);
		
		//Initialize Arrowhead Context
		arrowheadService.updateCoreServiceURIs(CoreSystem.ORCHESTRATOR);

		//TODO: implement here any custom behavior on application start up
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		//TODO: implement here any custom behavior on application shutdown
	}
}
