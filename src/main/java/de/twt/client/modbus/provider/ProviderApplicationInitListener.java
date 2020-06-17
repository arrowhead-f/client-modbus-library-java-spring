package de.twt.client.modbus.provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import de.twt.client.modbus.common.security.ModbusSecurityConfig;
import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	
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
	private String mySystemName;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int mySystemPort;
	
	@Value(ProviderConstants.$REQUEST_PARAM_SLAVEADDRESS)
	private String slaveAddress;
	
	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		logger.info("customInit: provider (slave \"{}\") starts...", slaveAddress);

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			
			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		}
		setTokenSecurityFilter();
		
		//Register services into ServiceRegistry
		// register read modbus data service
		
		final ServiceRegistryRequestDTO echoRequest = 
				createServiceRegistryRequest(
						ProviderConstants.ECHO_DEFINITION, 
						ProviderConstants.ECHO_URI, 
						ProviderConstants.ECHO_HTTP_METHOD);
		arrowheadService.forceRegisterServiceToServiceRegistry(echoRequest);
		
		// register read modbus data service
		final ServiceRegistryRequestDTO readModbusDataRequest = 
				createServiceRegistryRequest(
						ProviderConstants.READ_MODBUS_DATA_SERVICE_DEFINITION, 
						ProviderConstants.READ_MODBUS_DATA_URI, 
						ProviderConstants.READ_MODBUS_DATA_HTTP_METHOD);
		readModbusDataRequest.getMetadata().put(ProviderConstants.REQUEST_PARAM_KEY_SLAVEADDRESS, slaveAddress);
		arrowheadService.forceRegisterServiceToServiceRegistry(readModbusDataRequest);
		
		// register write modbus data service
		final ServiceRegistryRequestDTO writeModbusDataRequest = 
				createServiceRegistryRequest(
						ProviderConstants.WRITE_MODBUS_DATA_SERVICE_DEFINITION, 
						ProviderConstants.WRITE_MODBUS_DATA_URI, 
						ProviderConstants.WRITE_MODBUS_DATA_HTTP_METHOD);
		writeModbusDataRequest.getMetadata().put(ProviderConstants.REQUEST_PARAM_KEY_SLAVEADDRESS, slaveAddress);
		arrowheadService.forceRegisterServiceToServiceRegistry(writeModbusDataRequest);
		
		// register write modbus data csche service
		final ServiceRegistryRequestDTO setModbusDataCacheRequest = 
				createServiceRegistryRequest(
						ProviderConstants.SET_MODBUS_DATA_CACHE_SERVICE_DEFINITION, 
						ProviderConstants.SET_MODBUS_DATA_CACHE_URI, 
						ProviderConstants.SET_MODBUS_DATA_CACHE_HTTP_METHOD);
		setModbusDataCacheRequest.getMetadata().put(ProviderConstants.REQUEST_PARAM_KEY_SLAVEADDRESS, slaveAddress);
		arrowheadService.forceRegisterServiceToServiceRegistry(setModbusDataCacheRequest);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		logger.info("customDestroy: provider (slave \"{}\") stops...", slaveAddress);
		
		//Unregister service
		arrowheadService.unregisterServiceFromServiceRegistry(ProviderConstants.READ_MODBUS_DATA_SERVICE_DEFINITION);
		arrowheadService.unregisterServiceFromServiceRegistry(ProviderConstants.WRITE_MODBUS_DATA_SERVICE_DEFINITION);
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
			final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			securityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			securityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
		}
	}
	
	
	private ServiceRegistryRequestDTO createServiceRegistryRequest(
			final String serviceDefinition,
			final String serviceUri,
			final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);
		
		if (tokenSecurityFilterEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN.toString());
			serviceRegistryRequest.setInterfaces(Arrays.asList(ProviderConstants.INTERFACE_SECURE));
		} else if (sslEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE.toString());
			serviceRegistryRequest.setInterfaces(Arrays.asList(ProviderConstants.INTERFACE_SECURE));
		} else {
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE.toString());
			serviceRegistryRequest.setInterfaces(Arrays.asList(ProviderConstants.INTERFACE_INSECURE));
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put(ProviderConstants.HTTP_METHOD, httpMethod.name());
		return serviceRegistryRequest;
	}
}
