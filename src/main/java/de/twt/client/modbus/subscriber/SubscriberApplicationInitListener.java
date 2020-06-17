package de.twt.client.modbus.subscriber;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import de.twt.client.modbus.common.ModbusSystem;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.cache.ModbusSystemCacheManager;
import de.twt.client.modbus.common.security.ModbusSecurityConfig;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class SubscriberApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ModbusSecurityConfig securityConfig;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String clientSystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String clientSystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int clientSystemPort;
	
	private final Logger logger = LogManager.getLogger(SubscriberApplicationInitListener.class);
	
	@Autowired
	private SubscriberEventTypeURI subscriberEventTypeURI;
	
	@Autowired
	private ModbusSystemCacheManager modbusSystemCacheManager;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		logger.debug("run customInit...");
		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);
			
			setTokenSecurityFilter();
			setNotificationFilter();
		}
		
		if ( arrowheadService.echoCoreSystem(CoreSystem.EVENT_HANDLER)) {			
			arrowheadService.updateCoreServiceURIs(CoreSystem.EVENT_HANDLER);	
			subscribeToPresetEvents();			
		} else {
			logger.error("customInit: the event handler does not work in the core system!");
		}
		
		// set default input in den ModbusDataManager
		List<ModbusSystem.Module> modules = modbusSystemCacheManager.getHeadModules();
		for (ModbusSystem.Module module : modules) {
			if (module.getPreModuleName() == null || module.getPreModuleName() == "") {
				continue;
			}
			ModbusSystem.Module.DataInterface input = module.getInput();
			if (input == null) {
				logger.warn("The Modul does not have the input!");
				continue;
			}
			
			String slaveAddress = input.getSlaveAddress();
			int address = input.getAddress();
			String defaultValue = input.getDefaultValue();
			switch(input.getType()) {
			case coil: ModbusDataCacheManager.setCoil(slaveAddress, address, Boolean.valueOf(defaultValue)); break;
			case discreteInput: ModbusDataCacheManager.setDiscreteInput(slaveAddress, address, Boolean.valueOf(defaultValue)); break;
			case holdingRegister: ModbusDataCacheManager.setHoldingRegister(slaveAddress, address, Integer.valueOf(defaultValue)); break;
			case inputRegister: ModbusDataCacheManager.setInputRegister(slaveAddress, address, Integer.valueOf(defaultValue)); break;
			}
		}
		
		//TODO: implement here any custom behavior on application start up
	}


	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		logger.debug("run customDestroy...");
		final Map<String, String> eventTypeMap = subscriberEventTypeURI.getEventTypeURIMap();
		if( eventTypeMap == null) {			
			logger.info("No preset events to unsubscribe.");		
		} else {
			for (final String eventType : eventTypeMap.keySet()) {				
				arrowheadService.unsubscribeFromEventHandler(eventType, clientSystemName, clientSystemAddress, clientSystemPort);				
			}
		}
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		if(!tokenSecurityFilterEnabled || !sslEnabled) {
			logger.info("TokenSecurityFilter in not active");
		} else {
			final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
			if (authorizationPublicKey == null) {
				throw new ArrowheadException("Authorization public key is null");
			}
			
			KeyStore keystore;
			try {
				keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
				keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				throw new ArrowheadException(ex.getMessage());
			}			
			final PrivateKey subscriberPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			final Map<String, String> eventTypeMap = subscriberEventTypeURI.getEventTypeURIMap();

			securityConfig.getTokenSecurityFilter().setEventTypeMap( eventTypeMap );
			securityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			securityConfig.getTokenSecurityFilter().setMyPrivateKey(subscriberPrivateKey);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void subscribeToPresetEvents() {
		
		final Map<String, String> eventTypeMap = subscriberEventTypeURI.getEventTypeURIMap();
		
		if(eventTypeMap == null) {
			logger.info("No preset events to subscribe.");
			return;
		}
		final SystemRequestDTO subscriber = new SystemRequestDTO();
		subscriber.setSystemName(clientSystemName);
		subscriber.setAddress(clientSystemAddress);
		subscriber.setPort(clientSystemPort);
		if (sslEnabled) {
			subscriber.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
		}
		
		for (final String eventType : eventTypeMap.keySet()) {					
			try {					
				arrowheadService.unsubscribeFromEventHandler(eventType, clientSystemName, clientSystemAddress, clientSystemPort);				
			} catch (final Exception ex) {					
				logger.debug("Exception happend in subscription initalization " + ex);
			}
			
			try {
				arrowheadService.subscribeToEventHandler(
						SubscriberUtilities.createSubscriptionRequestDTO(eventType, subscriber, eventTypeMap.get(eventType)));				
			} catch ( final InvalidParameterException ex) {					
				if( ex.getMessage().contains("Subscription violates uniqueConstraint rules")) {						
					logger.debug("Subscription is already in DB");
				}
			} catch (final Exception ex) {
				logger.debug("Could not subscribe to EventType: " + eventType);
			} 
			logger.info("subscribe event {} with URI {} successfully!", eventType, eventTypeMap.get(eventType));
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private void setNotificationFilter() {
		logger.debug("setNotificationFilter started...");
		
		final Map<String, String> eventTypeMap = subscriberEventTypeURI.getEventTypeURIMap();

		securityConfig.getNotificationFilter().setEventTypeMap(eventTypeMap);
		securityConfig.getNotificationFilter().setServerCN(arrowheadService.getServerCN());		
	}
}
