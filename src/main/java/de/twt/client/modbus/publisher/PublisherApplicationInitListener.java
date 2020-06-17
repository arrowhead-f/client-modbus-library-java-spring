package de.twt.client.modbus.publisher;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
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
import eu.arrowhead.common.dto.shared.EventPublishRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

@Component
public class PublisherApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ModbusSecurityConfig securityConfig;
	
	@Autowired
	private ModbusSystemCacheManager modbusSystemCacheManager;
	
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
	
	private final Logger logger = LogManager.getLogger(PublisherApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		logger.debug("init the publisher...");
		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		}		
		
		setTokenSecurityFilter();
		
		if ( arrowheadService.echoCoreSystem(CoreSystem.EVENT_HANDLER) ) {
			
			arrowheadService.updateCoreServiceURIs(CoreSystem.EVENT_HANDLER);	
			
			publishInitStartedEvent();
		}
		
		
		
		
		//TODO: implement here any custom behavior on application start up
		setDefaultValueInModbusDataCacheBasedOnModbusSystem();
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		logger.debug("destroy the publisher...");
		//TODO: implement here any custom behavior on application shout down
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
			final PrivateKey publisherPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			securityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			securityConfig.getTokenSecurityFilter().setMyPrivateKey(publisherPrivateKey);
		}
	}

	//-------------------------------------------------------------------------------------------------	
	//Sample implementation of event publishing at application init time
	private void publishInitStartedEvent() {
		logger.info( "publishInitStartedEvent started..." );
		
		final String eventType = "StartEvent";
		
		final SystemRequestDTO source = new SystemRequestDTO();
		source.setSystemName(clientSystemName);
		source.setAddress(clientSystemAddress);
		source.setPort(clientSystemPort);
		if (sslEnabled) {	
			source.setAuthenticationInfo( Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
		}

		final Map<String,String> metadata = null;		
		final String payload = PublisherConstants.START_INIT_EVENT_PAYLOAD;		
		final String timeStamp = Utilities.convertZonedDateTimeToUTCString(ZonedDateTime.now());		
		final EventPublishRequestDTO publishRequestDTO = new EventPublishRequestDTO(eventType, source, metadata, payload, timeStamp);
		
		arrowheadService.publishToEventHandler(publishRequestDTO);				
	}

	private void setDefaultValueInModbusDataCacheBasedOnModbusSystem() {
		List<ModbusSystem.Module> tails = modbusSystemCacheManager.getTailModules();
		for (ModbusSystem.Module tail : tails) {
			ModbusSystem.Module.DataInterface output = tail.getOutput();
			if (tail.getNextModuleName() == null || tail.getNextModuleName() == "") {
				continue;
			}
			if (output == null) {
				logger.warn("there is no output data at the last module {}.", tail.getName());
				continue;
			}
			
			String slaveAddress = output.getSlaveAddress();
			int address = output.getAddress();
			String defaultValue = output.getDefaultValue();
			switch(output.getType()) {
			case coil: ModbusDataCacheManager.setCoil(slaveAddress, address, Boolean.valueOf(defaultValue)); break;
			case discreteInput: ModbusDataCacheManager.setDiscreteInput(slaveAddress, address, Boolean.valueOf(defaultValue)); break;
			case holdingRegister: ModbusDataCacheManager.setHoldingRegister(slaveAddress, address, Integer.valueOf(defaultValue)); break;
			case inputRegister: ModbusDataCacheManager.setInputRegister(slaveAddress, address, Integer.valueOf(defaultValue)); break;
			}
			
		}
	}
	
}
